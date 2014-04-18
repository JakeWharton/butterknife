package butterknife.internal;

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
      OnTextChanged.class //
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

  private boolean isValidForGeneratedCode(Class<? extends Annotation> annotationClass,
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
    hasError |= isValidForGeneratedCode(InjectView.class, "fields", element);

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
    String name = element.getSimpleName().toString();
    int id = element.getAnnotation(InjectView.class).value();
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
    TypeMirror erasedType = typeUtils.erasure(elementType);
    TypeMirror viewType = null;
    CollectionBinding.Kind kind = null;
    if (elementType.getKind() == TypeKind.ARRAY) {
      ArrayType arrayType = (ArrayType) elementType;
      viewType = arrayType.getComponentType();
      kind = CollectionBinding.Kind.ARRAY;
    } else if (LIST_TYPE.equals(erasedType.toString())) {
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
    hasError |= isValidForGeneratedCode(InjectViews.class, "fields", element);

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

    assert viewType != null; // Always false as hasError would have been true.
    String type = viewType.toString();

    ViewInjector viewInjector = getOrCreateTargetClass(targetClassMap, enclosingElement);
    CollectionBinding binding = new CollectionBinding(name, type, kind);
    viewInjector.addCollection(ids, binding);

    erasedTargetNames.add(enclosingElement.toString());
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
      error(element, "@%s annotation must be on a method.", annotationClass.getSimpleName());
      return;
    }

    ExecutableElement executableElement = (ExecutableElement) element;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Assemble information on the injection point.
    Annotation annotation = element.getAnnotation(annotationClass);
    Method annotationValue = annotationClass.getDeclaredMethod("value");
    if (annotationValue == null || annotationValue.getReturnType() != int[].class) {
      error(element, "@%s annotation lacks int[] value property. (%s.%s)", annotationClass,
          enclosingElement.getQualifiedName(), element.getSimpleName());
      return;
    }
    Method annotationCallback = annotationClass.getDeclaredMethod("callback");
    if (annotationCallback == null) {
      error(element, "@%s annotation lacks callback property. (%s.%s)", annotationClass,
          enclosingElement.getQualifiedName(), element.getSimpleName());
      return;
    }

    int[] ids = (int[]) annotationValue.invoke(annotation);
    Enum<?> callback = (Enum<?>) annotationCallback.invoke(annotation);
    String name = executableElement.getSimpleName().toString();
    boolean required = element.getAnnotation(Optional.class) == null;

    // Verify that the method and its containing class are accessible via generated code.
    boolean hasError = isValidForGeneratedCode(annotationClass, "methods", element);

    Set<Integer> seenIds = new LinkedHashSet<Integer>();
    for (int id : ids) {
      if (!seenIds.add(id)) {
        error(element, "@%s annotation for method contains duplicate ID %d. (%s.%s)",
            annotationClass.getSimpleName(), id, enclosingElement.getQualifiedName(),
            element.getSimpleName());
        hasError = true;
      }
    }

    ListenerClass listener = annotationClass.getAnnotation(ListenerClass.class);
    if (listener == null) {
      error(element, "No @%s defined on @%s.", ListenerClass.class.getSimpleName(),
          annotationClass.getSimpleName());
      return; // We can't do any more validation without a listener.
    }

    Field callbackField = callback.getDeclaringClass().getField(callback.name());
    ListenerMethod method = callbackField.getAnnotation(ListenerMethod.class);
    if (method == null) {
      error(element, "No @%s defined on @%s's %s.%s.", ListenerMethod.class.getSimpleName(),
          annotationClass.getSimpleName(), callback.getDeclaringClass().getSimpleName(),
          callback.name());
      return; // We can't do any more validation without a method.
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
        error(element, "Multiple @%s methods declared for ID %s in %s.",
            annotationClass.getSimpleName(), id, enclosingElement.getQualifiedName());
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
