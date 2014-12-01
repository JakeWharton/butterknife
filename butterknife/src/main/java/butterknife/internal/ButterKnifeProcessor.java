package butterknife.internal;

import android.view.View;
import butterknife.InjectView;
import butterknife.InjectViews;
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
import butterknife.Optional;
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
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;

public final class ButterKnifeProcessor extends AbstractProcessor {
  public static final String SUFFIX = "$$ViewInjector";
  public static final String ANDROID_PREFIX = "android.";
  public static final String JAVA_PREFIX = "java.";
  static final String VIEW_TYPE = "android.view.View";
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
    supportTypes.add(InjectView.class.getCanonicalName());
    supportTypes.add(InjectViews.class.getCanonicalName());
    for (Class<? extends Annotation> listener : LISTENERS) {
      supportTypes.add(listener.getCanonicalName());
    }

    return supportTypes;
  }

  @Override public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
    Map<TypeElement, ViewInjector> targetClassMap = findAndParseTargets(env);

    for (Map.Entry<TypeElement, ViewInjector> entry : targetClassMap.entrySet()) {
      TypeElement typeElement = entry.getKey();
      ViewInjector viewInjector = entry.getValue();

      try {
        JavaFileObject jfo = filer.createSourceFile(viewInjector.getFqcn(), typeElement);
        Writer writer = jfo.openWriter();
        writer.write(viewInjector.brewJava());
        writer.flush();
        writer.close();
      } catch (IOException e) {
        error(typeElement, "Unable to write injector for type %s: %s", typeElement, e.getMessage());
      }
    }

    return true;
  }

  private Map<TypeElement, ViewInjector> findAndParseTargets(RoundEnvironment env) {
    Map<TypeElement, ViewInjector> targetClassMap = new LinkedHashMap<TypeElement, ViewInjector>();
    Set<String> erasedTargetNames = new LinkedHashSet<String>();

    // Process each @InjectView element.
    for (Element element : env.getElementsAnnotatedWith(InjectView.class)) {
      try {
        parseInjectView(element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));

        error(element, "Unable to generate view injector for @InjectView.\n\n%s", stackTrace);
      }
    }

    // Process each @InjectViews element.
    for (Element element : env.getElementsAnnotatedWith(InjectViews.class)) {
      try {
        parseInjectViews(element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));

        error(element, "Unable to generate view injector for @InjectViews.\n\n%s", stackTrace);
      }
    }

    // Process each annotation that corresponds to a listener.
    for (Class<? extends Annotation> listener : LISTENERS) {
      findAndParseListener(env, listener, targetClassMap, erasedTargetNames);
    }

    // Try to find a parent injector for each injector.
    for (Map.Entry<TypeElement, ViewInjector> entry : targetClassMap.entrySet()) {
      String parentClassFqcn = findParentFqcn(entry.getKey(), erasedTargetNames);
      if (parentClassFqcn != null) {
        entry.getValue().setParentInjector(parentClassFqcn + SUFFIX);
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

  private void parseInjectView(Element element, Map<TypeElement, ViewInjector> targetClassMap,
      Set<String> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type extends from View.
    TypeMirror elementType = element.asType();
    if (elementType instanceof TypeVariable) {
      TypeVariable typeVariable = (TypeVariable) elementType;
      elementType = typeVariable.getUpperBound();
    }
    if (!isSubtypeOfType(elementType, VIEW_TYPE)) {
      error(element, "@InjectView fields must extend from View. (%s.%s)",
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(InjectView.class, "fields", element);
    hasError |= isBindingInWrongPackage(InjectView.class, element);

    // Check for the other field annotation.
    if (element.getAnnotation(InjectViews.class) != null) {
      error(element, "Only one of @InjectView and @InjectViews is allowed. (%s.%s)",
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    if (hasError) {
      return;
    }

    // Assemble information on the injection point.
    int id = element.getAnnotation(InjectView.class).value();

    ViewInjector injector = targetClassMap.get(enclosingElement);
    if (injector != null) {
      ViewInjection viewInjection = injector.getViewInjection(id);
      if (viewInjection != null) {
        Iterator<ViewBinding> iterator = viewInjection.getViewBindings().iterator();
        if (iterator.hasNext()) {
          ViewBinding existingBinding = iterator.next();
          error(element,
              "Attempt to use @InjectView for an already injected ID %d on '%s'. (%s.%s)", id,
              existingBinding.getName(), enclosingElement.getQualifiedName(),
              element.getSimpleName());
          return;
        }
      }
    }

    String name = element.getSimpleName().toString();
    String type = elementType.toString();
    boolean required = element.getAnnotation(Optional.class) == null;

    ViewInjector viewInjector = getOrCreateTargetClass(targetClassMap, enclosingElement);
    ViewBinding binding = new ViewBinding(name, type, required);
    viewInjector.addView(id, binding);

    // Add the type-erased version to the valid injection targets set.
    erasedTargetNames.add(enclosingElement.toString());
  }

  private void parseInjectViews(Element element, Map<TypeElement, ViewInjector> targetClassMap,
      Set<String> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the type is a List or an array.
    TypeMirror elementType = element.asType();
    String erasedType = doubleErasure(elementType);
    TypeMirror viewType = null;
    CollectionBinding.Kind kind = null;
    if (elementType.getKind() == TypeKind.ARRAY) {
      ArrayType arrayType = (ArrayType) elementType;
      viewType = arrayType.getComponentType();
      kind = CollectionBinding.Kind.ARRAY;
    } else if (LIST_TYPE.equals(erasedType)) {
      DeclaredType declaredType = (DeclaredType) elementType;
      List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
      if (typeArguments.size() != 1) {
        error(element, "@InjectViews List must have a generic component. (%s.%s)",
            enclosingElement.getQualifiedName(), element.getSimpleName());
        hasError = true;
      } else {
        viewType = typeArguments.get(0);
      }
      kind = CollectionBinding.Kind.LIST;
    } else {
      error(element, "@InjectViews must be a List or array. (%s.%s)",
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }
    if (viewType instanceof TypeVariable) {
      TypeVariable typeVariable = (TypeVariable) viewType;
      viewType = typeVariable.getUpperBound();
    }

    // Verify that the target type extends from View.
    if (viewType != null && !isSubtypeOfType(viewType, VIEW_TYPE)) {
      error(element, "@InjectViews type must extend from View. (%s.%s)",
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(InjectViews.class, "fields", element);
    hasError |= isBindingInWrongPackage(InjectViews.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the injection point.
    String name = element.getSimpleName().toString();
    int[] ids = element.getAnnotation(InjectViews.class).value();
    if (ids.length == 0) {
      error(element, "@InjectViews must specify at least one ID. (%s.%s)",
          enclosingElement.getQualifiedName(), element.getSimpleName());
      return;
    }

    Integer duplicateId = findDuplicate(ids);
    if (duplicateId != null) {
      error(element, "@InjectViews annotation contains duplicate ID %d. (%s.%s)", duplicateId,
          enclosingElement.getQualifiedName(), element.getSimpleName());
    }

    assert viewType != null; // Always false as hasError would have been true.
    String type = viewType.toString();
    boolean required = element.getAnnotation(Optional.class) == null;

    ViewInjector viewInjector = getOrCreateTargetClass(targetClassMap, enclosingElement);
    CollectionBinding binding = new CollectionBinding(name, type, kind, required);
    viewInjector.addCollection(ids, binding);

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
      Class<? extends Annotation> annotationClass, Map<TypeElement, ViewInjector> targetClassMap,
      Set<String> erasedTargetNames) {
    for (Element element : env.getElementsAnnotatedWith(annotationClass)) {
      try {
        parseListenerAnnotation(annotationClass, element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));

        error(element, "Unable to generate view injector for @%s.\n\n%s",
            annotationClass.getSimpleName(), stackTrace.toString());
      }
    }
  }

  private void parseListenerAnnotation(Class<? extends Annotation> annotationClass, Element element,
      Map<TypeElement, ViewInjector> targetClassMap, Set<String> erasedTargetNames)
      throws Exception {
    // This should be guarded by the annotation's @Target but it's worth a check for safe casting.
    if (!(element instanceof ExecutableElement) || element.getKind() != METHOD) {
      throw new IllegalStateException(
          String.format("@%s annotation must be on a method.", annotationClass.getSimpleName()));
    }

    ExecutableElement executableElement = (ExecutableElement) element;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Assemble information on the injection point.
    Annotation annotation = element.getAnnotation(annotationClass);
    Method annotationValue = annotationClass.getDeclaredMethod("value");
    if (annotationValue.getReturnType() != int[].class) {
      throw new IllegalStateException(
          String.format("@%s annotation value() type not int[].", annotationClass));
    }

    int[] ids = (int[]) annotationValue.invoke(annotation);
    String name = executableElement.getSimpleName().toString();
    boolean required = element.getAnnotation(Optional.class) == null;

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
            error(element, "ID free injection must not be annotated with @Optional. (%s.%s)",
                enclosingElement.getQualifiedName(), element.getSimpleName());
            hasError = true;
          }

          // Verify target type is valid for a binding without an id.
          String targetType = listener.targetType();
          if (!isSubtypeOfType(enclosingElement.asType(), targetType)) {
            error(element, "@%s annotation without an ID may only be used with an object of type "
                    + "\"%s\". (%s.%s)", annotationClass.getSimpleName(), targetType,
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
          if (isSubtypeOfType(methodParameterType, parameterTypes[j])) {
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

    ListenerBinding binding = new ListenerBinding(name, Arrays.asList(parameters), required);
    ViewInjector viewInjector = getOrCreateTargetClass(targetClassMap, enclosingElement);
    for (int id : ids) {
      if (!viewInjector.addListener(id, listener, method, binding)) {
        error(element, "Multiple listener methods with return value specified for ID %d. (%s.%s)",
            id, enclosingElement.getQualifiedName(), element.getSimpleName());
        return;
      }
    }

    // Add the type-erased version to the valid injection targets set.
    erasedTargetNames.add(enclosingElement.toString());
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

  private ViewInjector getOrCreateTargetClass(Map<TypeElement, ViewInjector> targetClassMap,
      TypeElement enclosingElement) {
    ViewInjector viewInjector = targetClassMap.get(enclosingElement);
    if (viewInjector == null) {
      String targetType = enclosingElement.getQualifiedName().toString();
      String classPackage = getPackageName(enclosingElement);
      String className = getClassName(enclosingElement, classPackage) + SUFFIX;

      viewInjector = new ViewInjector(classPackage, className, targetType);
      targetClassMap.put(enclosingElement, viewInjector);
    }
    return viewInjector;
  }

  private static String getClassName(TypeElement type, String packageName) {
    int packageLen = packageName.length() + 1;
    return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
  }

  /** Finds the parent injector type in the supplied set, if any. */
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
}
