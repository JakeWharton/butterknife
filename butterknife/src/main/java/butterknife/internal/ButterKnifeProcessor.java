package butterknife.internal;

import android.view.View;
import butterknife.FindView;
import butterknife.FindViews;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import butterknife.OnItemSelected;
import butterknife.OnLongClick;
import butterknife.OnPageChange;
import butterknife.OnTextChanged;
import butterknife.OnTouch;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;

public final class ButterKnifeProcessor extends AbstractProcessor {
  public static final String SUFFIX = "$$ViewBinder";
  public static final String ANDROID_PREFIX = "android.";
  public static final String JAVA_PREFIX = "java.";
  static final String VIEW_TYPE = "android.view.View";
  private static final String NULLABLE_ANNOTATION_NAME = "Nullable";
  private static final String LIST_TYPE = List.class.getCanonicalName();
  private static final List<Class<? extends Annotation>> LISTENERS = Arrays.asList(//
      OnCheckedChanged.class, //
      OnClick.class, //
      OnEditorAction.class, //
      OnFocusChange.class, //
      OnItemClick.class, //
      OnItemLongClick.class, //
      OnItemSelected.class, //
      OnLongClick.class, //
      OnPageChange.class, //
      OnTextChanged.class, //
      OnTouch.class //
  );

  private Elements elementUtils;
  private Types typeUtils;
  private Filer filer;

  @Override public synchronized void init(ProcessingEnvironment env) {
    super.init(env);

    elementUtils = env.getElementUtils();
    typeUtils = env.getTypeUtils();
    filer = env.getFiler();
  }

  @Override public Set<String> getSupportedAnnotationTypes() {
    Set<String> supportTypes = new LinkedHashSet<String>();
    supportTypes.add(FindView.class.getCanonicalName());
    supportTypes.add(FindViews.class.getCanonicalName());
    for (Class<? extends Annotation> listener : LISTENERS) {
      supportTypes.add(listener.getCanonicalName());
    }

    return supportTypes;
  }

  @Override public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
    Map<TypeElement, BindingClass> targetClassMap = findAndParseTargets(env);

    for (Map.Entry<TypeElement, BindingClass> entry : targetClassMap.entrySet()) {
      TypeElement typeElement = entry.getKey();
      BindingClass bindingClass = entry.getValue();

      try {
        JavaFileObject jfo = filer.createSourceFile(bindingClass.getFqcn(), typeElement);
        Writer writer = jfo.openWriter();
        writer.write(bindingClass.brewJava());
        writer.flush();
        writer.close();
      } catch (IOException e) {
        error(typeElement, "Unable to write view binder for type %s: %s", typeElement,
            e.getMessage());
      }
    }

    return true;
  }

  private Map<TypeElement, BindingClass> findAndParseTargets(RoundEnvironment env) {
    Map<TypeElement, BindingClass> targetClassMap = new LinkedHashMap<TypeElement, BindingClass>();
    Set<String> erasedTargetNames = new LinkedHashSet<String>();

    // Process each @FindView element.
    for (Element element : env.getElementsAnnotatedWith(FindView.class)) {
      try {
        parseFindView(element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));

        error(element, "Unable to generate view binder for @FindView.\n\n%s", stackTrace);
      }
    }

    // Process each @FindViews element.
    for (Element element : env.getElementsAnnotatedWith(FindViews.class)) {
      try {
        parseFindViews(element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));

        error(element, "Unable to generate view binder for @FindViews.\n\n%s", stackTrace);
      }
    }

    // Process each annotation that corresponds to a listener.
    for (Class<? extends Annotation> listener : LISTENERS) {
      findAndParseListener(env, listener, targetClassMap, erasedTargetNames);
    }

    // Try to find a parent binder for each.
    for (Map.Entry<TypeElement, BindingClass> entry : targetClassMap.entrySet()) {
      String parentClassFqcn = findParentFqcn(entry.getKey(), erasedTargetNames);
      if (parentClassFqcn != null) {
        entry.getValue().setParentViewBinder(parentClassFqcn + SUFFIX);
      }
    }

    return targetClassMap;
  }

  private boolean isInaccessibleViaGeneratedCode(Class<? extends Annotation> annotationClass,
      String targetThing, Element element) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify method modifiers.
    Set<Modifier> modifiers = element.getModifiers();
    if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
      error(element, "@%s %s must not be private or static. (%s.%s)",
          annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify containing type.
    if (enclosingElement.getKind() != CLASS) {
      error(enclosingElement, "@%s %s may only be contained in classes. (%s.%s)",
          annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify containing class visibility is not private.
    if (enclosingElement.getModifiers().contains(PRIVATE)) {
      error(enclosingElement, "@%s %s may not be contained in private classes. (%s.%s)",
          annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    return hasError;
  }

  private boolean isBindingInWrongPackage(Class<? extends Annotation> annotationClass,
      Element element) {
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
    String qualifiedName = enclosingElement.getQualifiedName().toString();

    if (qualifiedName.startsWith(ANDROID_PREFIX)) {
      error(element, "@%s-annotated class incorrectly in Android framework package. (%s)",
          annotationClass.getSimpleName(), qualifiedName);
      return true;
    }
    if (qualifiedName.startsWith(JAVA_PREFIX)) {
      error(element, "@%s-annotated class incorrectly in Java framework package. (%s)",
          annotationClass.getSimpleName(), qualifiedName);
      return true;
    }

    return false;
  }

  private void parseFindView(Element element, Map<TypeElement, BindingClass> targetClassMap,
      Set<String> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type extends from View.
    TypeMirror elementType = element.asType();
    if (elementType instanceof TypeVariable) {
      TypeVariable typeVariable = (TypeVariable) elementType;
      elementType = typeVariable.getUpperBound();
    }
    if (!isSubtypeOfType(elementType, VIEW_TYPE) && !isInterface(elementType)) {
      error(element, "@FindView fields must extend from View or be an interface. (%s.%s)",
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(FindView.class, "fields", element);
    hasError |= isBindingInWrongPackage(FindView.class, element);

    // Check for the other field annotation.
    if (element.getAnnotation(FindViews.class) != null) {
      error(element, "Only one of @FindView and @FindViews is allowed. (%s.%s)",
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    int id = element.getAnnotation(FindView.class).value();

    BindingClass bindingClass = targetClassMap.get(enclosingElement);
    if (bindingClass != null) {
      ViewBindings viewBindings = bindingClass.getViewInjection(id);
      if (viewBindings != null) {
        Iterator<FieldBinding> iterator = viewBindings.getFieldBindings().iterator();
        if (iterator.hasNext()) {
          FieldBinding existingBinding = iterator.next();
          error(element,
              "Attempt to use @FindView for an already bound ID %d on '%s'. (%s.%s)", id,
              existingBinding.getName(), enclosingElement.getQualifiedName(),
              element.getSimpleName());
          return;
        }
      }
    } else {
      bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
    }

    String name = element.getSimpleName().toString();
    String type = elementType.toString();
    boolean required = isRequiredInjection(element);

    FieldBinding binding = new FieldBinding(name, type, required);
    bindingClass.addField(id, binding);

    // Add the type-erased version to the valid binding targets set.
    erasedTargetNames.add(enclosingElement.toString());
  }

  private void parseFindViews(Element element, Map<TypeElement, BindingClass> targetClassMap,
      Set<String> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the type is a List or an array.
    TypeMirror elementType = element.asType();
    String erasedType = doubleErasure(elementType);
    TypeMirror viewType = null;
    FieldCollectionBinding.Kind kind = null;
    if (elementType.getKind() == TypeKind.ARRAY) {
      ArrayType arrayType = (ArrayType) elementType;
      viewType = arrayType.getComponentType();
      kind = FieldCollectionBinding.Kind.ARRAY;
    } else if (LIST_TYPE.equals(erasedType)) {
      DeclaredType declaredType = (DeclaredType) elementType;
      List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
      if (typeArguments.size() != 1) {
        error(element, "@FindViews List must have a generic component. (%s.%s)",
            enclosingElement.getQualifiedName(), element.getSimpleName());
        hasError = true;
      } else {
        viewType = typeArguments.get(0);
      }
      kind = FieldCollectionBinding.Kind.LIST;
    } else {
      error(element, "@FindViews must be a List or array. (%s.%s)",
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }
    if (viewType instanceof TypeVariable) {
      TypeVariable typeVariable = (TypeVariable) viewType;
      viewType = typeVariable.getUpperBound();
    }

    // Verify that the target type extends from View.
    if (viewType != null && !isSubtypeOfType(viewType, VIEW_TYPE) && !isInterface(viewType)) {
      error(element, "@FindViews type must extend from View or be an interface. (%s.%s)",
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(FindViews.class, "fields", element);
    hasError |= isBindingInWrongPackage(FindViews.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    int[] ids = element.getAnnotation(FindViews.class).value();
    if (ids.length == 0) {
      error(element, "@FindViews must specify at least one ID. (%s.%s)",
          enclosingElement.getQualifiedName(), element.getSimpleName());
      return;
    }

    Integer duplicateId = findDuplicate(ids);
    if (duplicateId != null) {
      error(element, "@FindViews annotation contains duplicate ID %d. (%s.%s)", duplicateId,
          enclosingElement.getQualifiedName(), element.getSimpleName());
    }

    assert viewType != null; // Always false as hasError would have been true.
    String type = viewType.toString();
    boolean required = isRequiredInjection(element);

    BindingClass bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
    FieldCollectionBinding binding = new FieldCollectionBinding(name, type, kind, required);
    bindingClass.addFieldCollection(ids, binding);

    erasedTargetNames.add(enclosingElement.toString());
  }

  /** Returns the first duplicate element inside an array, null if there are no duplicates. */
  private static Integer findDuplicate(int[] array) {
    Set<Integer> seenElements = new LinkedHashSet<Integer>();

    for (int element : array) {
      if (!seenElements.add(element)) {
        return element;
      }
    }

    return null;
  }

  /** Uses both {@link Types#erasure} and string manipulation to strip any generic types. */
  private String doubleErasure(TypeMirror elementType) {
    String name = typeUtils.erasure(elementType).toString();
    int typeParamStart = name.indexOf('<');
    if (typeParamStart != -1) {
      name = name.substring(0, typeParamStart);
    }
    return name;
  }

  private void findAndParseListener(RoundEnvironment env,
      Class<? extends Annotation> annotationClass, Map<TypeElement, BindingClass> targetClassMap,
      Set<String> erasedTargetNames) {
    for (Element element : env.getElementsAnnotatedWith(annotationClass)) {
      try {
        parseListenerAnnotation(annotationClass, element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));

        error(element, "Unable to generate view binder for @%s.\n\n%s",
            annotationClass.getSimpleName(), stackTrace.toString());
      }
    }
  }

  private void parseListenerAnnotation(Class<? extends Annotation> annotationClass, Element element,
      Map<TypeElement, BindingClass> targetClassMap, Set<String> erasedTargetNames)
      throws Exception {
    // This should be guarded by the annotation's @Target but it's worth a check for safe casting.
    if (!(element instanceof ExecutableElement) || element.getKind() != METHOD) {
      throw new IllegalStateException(
          String.format("@%s annotation must be on a method.", annotationClass.getSimpleName()));
    }

    ExecutableElement executableElement = (ExecutableElement) element;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Assemble information on the method.
    Annotation annotation = element.getAnnotation(annotationClass);
    Method annotationValue = annotationClass.getDeclaredMethod("value");
    if (annotationValue.getReturnType() != int[].class) {
      throw new IllegalStateException(
          String.format("@%s annotation value() type not int[].", annotationClass));
    }

    int[] ids = (int[]) annotationValue.invoke(annotation);
    String name = executableElement.getSimpleName().toString();
    boolean required = isRequiredInjection(element);

    // Verify that the method and its containing class are accessible via generated code.
    boolean hasError = isInaccessibleViaGeneratedCode(annotationClass, "methods", element);
    hasError |= isBindingInWrongPackage(annotationClass, element);

    Integer duplicateId = findDuplicate(ids);
    if (duplicateId != null) {
      error(element, "@%s annotation for method contains duplicate ID %d. (%s.%s)",
          annotationClass.getSimpleName(), duplicateId, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    ListenerClass listener = annotationClass.getAnnotation(ListenerClass.class);
    if (listener == null) {
      throw new IllegalStateException(
          String.format("No @%s defined on @%s.", ListenerClass.class.getSimpleName(),
              annotationClass.getSimpleName()));
    }

    for (int id : ids) {
      if (id == View.NO_ID) {
        if (ids.length == 1) {
          if (!required) {
            error(element, "ID free binding must not be annotated with @Nullable. (%s.%s)",
                enclosingElement.getQualifiedName(), element.getSimpleName());
            hasError = true;
          }

          // Verify target type is valid for a binding without an id.
          String targetType = listener.targetType();
          if (!isSubtypeOfType(enclosingElement.asType(), targetType)
              && !isInterface(enclosingElement.asType())) {
            error(element, "@%s annotation without an ID may only be used with an object of type "
                    + "\"%s\" or an interface. (%s.%s)",
                    annotationClass.getSimpleName(), targetType,
                enclosingElement.getQualifiedName(), element.getSimpleName());
            hasError = true;
          }
        } else {
          error(element, "@%s annotation contains invalid ID %d. (%s.%s)",
              annotationClass.getSimpleName(), id, enclosingElement.getQualifiedName(),
              element.getSimpleName());
          hasError = true;
        }
      }
    }

    ListenerMethod method;
    ListenerMethod[] methods = listener.method();
    if (methods.length > 1) {
      throw new IllegalStateException(String.format("Multiple listener methods specified on @%s.",
          annotationClass.getSimpleName()));
    } else if (methods.length == 1) {
      if (listener.callbacks() != ListenerClass.NONE.class) {
        throw new IllegalStateException(
            String.format("Both method() and callback() defined on @%s.",
                annotationClass.getSimpleName()));
      }
      method = methods[0];
    } else {
      Method annotationCallback = annotationClass.getDeclaredMethod("callback");
      Enum<?> callback = (Enum<?>) annotationCallback.invoke(annotation);
      Field callbackField = callback.getDeclaringClass().getField(callback.name());
      method = callbackField.getAnnotation(ListenerMethod.class);
      if (method == null) {
        throw new IllegalStateException(
            String.format("No @%s defined on @%s's %s.%s.", ListenerMethod.class.getSimpleName(),
                annotationClass.getSimpleName(), callback.getDeclaringClass().getSimpleName(),
                callback.name()));
      }
    }

    // Verify that the method has equal to or less than the number of parameters as the listener.
    List<? extends VariableElement> methodParameters = executableElement.getParameters();
    if (methodParameters.size() > method.parameters().length) {
      error(element, "@%s methods can have at most %s parameter(s). (%s.%s)",
          annotationClass.getSimpleName(), method.parameters().length,
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    // Verify method return type matches the listener.
    TypeMirror returnType = executableElement.getReturnType();
    if (returnType instanceof TypeVariable) {
      TypeVariable typeVariable = (TypeVariable) returnType;
      returnType = typeVariable.getUpperBound();
    }
    if (!returnType.toString().equals(method.returnType())) {
      error(element, "@%s methods must have a '%s' return type. (%s.%s)",
          annotationClass.getSimpleName(), method.returnType(),
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    if (hasError) {
      return;
    }

    Parameter[] parameters = Parameter.NONE;
    if (!methodParameters.isEmpty()) {
      parameters = new Parameter[methodParameters.size()];
      BitSet methodParameterUsed = new BitSet(methodParameters.size());
      String[] parameterTypes = method.parameters();
      for (int i = 0; i < methodParameters.size(); i++) {
        VariableElement methodParameter = methodParameters.get(i);
        TypeMirror methodParameterType = methodParameter.asType();
        if (methodParameterType instanceof TypeVariable) {
          TypeVariable typeVariable = (TypeVariable) methodParameterType;
          methodParameterType = typeVariable.getUpperBound();
        }

        for (int j = 0; j < parameterTypes.length; j++) {
          if (methodParameterUsed.get(j)) {
            continue;
          }
          if (isSubtypeOfType(methodParameterType, parameterTypes[j])
              || isInterface(methodParameterType)) {
            parameters[i] = new Parameter(j, methodParameterType.toString());
            methodParameterUsed.set(j);
            break;
          }
        }
        if (parameters[i] == null) {
          StringBuilder builder = new StringBuilder();
          builder.append("Unable to match @")
              .append(annotationClass.getSimpleName())
              .append(" method arguments. (")
              .append(enclosingElement.getQualifiedName())
              .append('.')
              .append(element.getSimpleName())
              .append(')');
          for (int j = 0; j < parameters.length; j++) {
            Parameter parameter = parameters[j];
            builder.append("\n\n  Parameter #")
                .append(j + 1)
                .append(": ")
                .append(methodParameters.get(j).asType().toString())
                .append("\n    ");
            if (parameter == null) {
              builder.append("did not match any listener parameters");
            } else {
              builder.append("matched listener parameter #")
                  .append(parameter.getListenerPosition() + 1)
                  .append(": ")
                  .append(parameter.getType());
            }
          }
          builder.append("\n\nMethods may have up to ")
              .append(method.parameters().length)
              .append(" parameter(s):\n");
          for (String parameterType : method.parameters()) {
            builder.append("\n  ").append(parameterType);
          }
          builder.append(
              "\n\nThese may be listed in any order but will be searched for from top to bottom.");
          error(executableElement, builder.toString());
          return;
        }
      }
    }

    MethodBinding binding = new MethodBinding(name, Arrays.asList(parameters), required);
    BindingClass bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
    for (int id : ids) {
      if (!bindingClass.addMethod(id, listener, method, binding)) {
        error(element, "Multiple listener methods with return value specified for ID %d. (%s.%s)",
            id, enclosingElement.getQualifiedName(), element.getSimpleName());
        return;
      }
    }

    // Add the type-erased version to the valid binding targets set.
    erasedTargetNames.add(enclosingElement.toString());
  }

  private boolean isInterface(TypeMirror typeMirror) {
    if (!(typeMirror instanceof DeclaredType)) {
      return false;
    }
    return ((DeclaredType) typeMirror).asElement().getKind() == INTERFACE;
  }

  private boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
    if (otherType.equals(typeMirror.toString())) {
      return true;
    }
    if (!(typeMirror instanceof DeclaredType)) {
      return false;
    }
    DeclaredType declaredType = (DeclaredType) typeMirror;
    List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
    if (typeArguments.size() > 0) {
      StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
      typeString.append('<');
      for (int i = 0; i < typeArguments.size(); i++) {
        if (i > 0) {
          typeString.append(',');
        }
        typeString.append('?');
      }
      typeString.append('>');
      if (typeString.toString().equals(otherType)) {
        return true;
      }
    }
    Element element = declaredType.asElement();
    if (!(element instanceof TypeElement)) {
      return false;
    }
    TypeElement typeElement = (TypeElement) element;
    TypeMirror superType = typeElement.getSuperclass();
    if (isSubtypeOfType(superType, otherType)) {
      return true;
    }
    for (TypeMirror interfaceType : typeElement.getInterfaces()) {
      if (isSubtypeOfType(interfaceType, otherType)) {
        return true;
      }
    }
    return false;
  }

  private BindingClass getOrCreateTargetClass(Map<TypeElement, BindingClass> targetClassMap,
      TypeElement enclosingElement) {
    BindingClass bindingClass = targetClassMap.get(enclosingElement);
    if (bindingClass == null) {
      String targetType = enclosingElement.getQualifiedName().toString();
      String classPackage = getPackageName(enclosingElement);
      String className = getClassName(enclosingElement, classPackage) + SUFFIX;

      bindingClass = new BindingClass(classPackage, className, targetType);
      targetClassMap.put(enclosingElement, bindingClass);
    }
    return bindingClass;
  }

  private static String getClassName(TypeElement type, String packageName) {
    int packageLen = packageName.length() + 1;
    return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
  }

  /** Finds the parent binder type in the supplied set, if any. */
  private String findParentFqcn(TypeElement typeElement, Set<String> parents) {
    TypeMirror type;
    while (true) {
      type = typeElement.getSuperclass();
      if (type.getKind() == TypeKind.NONE) {
        return null;
      }
      typeElement = (TypeElement) ((DeclaredType) type).asElement();
      if (parents.contains(typeElement.toString())) {
        String packageName = getPackageName(typeElement);
        return packageName + "." + getClassName(typeElement, packageName);
      }
    }
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  private void error(Element element, String message, Object... args) {
    if (args.length > 0) {
      message = String.format(message, args);
    }
    processingEnv.getMessager().printMessage(ERROR, message, element);
  }

  private String getPackageName(TypeElement type) {
    return elementUtils.getPackageOf(type).getQualifiedName().toString();
  }

  private static boolean hasAnnotationWithName(Element element, String simpleName) {
    for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
      String annotationName = mirror.getAnnotationType().asElement().getSimpleName().toString();
      if (simpleName.equals(annotationName)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isRequiredInjection(Element element) {
    return !hasAnnotationWithName(element, NULLABLE_ANNOTATION_NAME);
  }
}
