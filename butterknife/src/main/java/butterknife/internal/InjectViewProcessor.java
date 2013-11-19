package butterknife.internal;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Optional;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;

@SupportedAnnotationTypes({ //
    "butterknife.InjectView", //
    "butterknife.OnClick", //
    "butterknife.OnItemClick" //
})
public class InjectViewProcessor extends AbstractProcessor {
  public static final String SUFFIX = "$$ViewInjector";
  static final String VIEW_TYPE = "android.view.View";
  static final Map<String, InjectableListenerHandler> LISTENER_HANDLER_MAP =
      new LinkedHashMap<String, InjectableListenerHandler>();

  private Elements elementUtils;
  private Types typeUtils;
  private Filer filer;

  @Override public synchronized void init(ProcessingEnvironment env) {
    super.init(env);

    elementUtils = env.getElementUtils();
    typeUtils = env.getTypeUtils();
    filer = env.getFiler();
  }

  @Override public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
    Map<TypeElement, TargetClass> targetClassMap = findAndParseTargets(env);

    for (Map.Entry<TypeElement, TargetClass> entry : targetClassMap.entrySet()) {
      TypeElement typeElement = entry.getKey();
      TargetClass targetClass = entry.getValue();

      // Write the view injector class.
      try {
        JavaFileObject jfo = filer.createSourceFile(targetClass.getFqcn(), typeElement);
        Writer writer = jfo.openWriter();
        writer.write(targetClass.brewJava());
        writer.flush();
        writer.close();
      } catch (IOException e) {
        error(typeElement, "Unable to write injector for type %s: %s", typeElement, e.getMessage());
      }
    }

    return true;
  }

  private Map<TypeElement, TargetClass> findAndParseTargets(RoundEnvironment env) {
    Map<TypeElement, TargetClass> targetClassMap = new LinkedHashMap<TypeElement, TargetClass>();
    Set<TypeMirror> erasedTargetTypes = new LinkedHashSet<TypeMirror>();

    // Process each @InjectView elements.
    for (Element element : env.getElementsAnnotatedWith(InjectView.class)) {
      try {
        parseInjectView(element, targetClassMap, erasedTargetTypes);
      } catch (Exception e) {
        error(element, "Unable to parse @InjectView: %s", e.getMessage());
      }
    }

    // Process each annotation that corresponds to a listener.
    findAndParseListener(env, OnClick.class, targetClassMap, erasedTargetTypes);
    findAndParseListener(env, OnItemClick.class, targetClassMap, erasedTargetTypes);

    // Try to find a parent injector for each injector.
    for (Map.Entry<TypeElement, TargetClass> entry : targetClassMap.entrySet()) {
      String parentClassFqcn = findParentFqcn(entry.getKey(), erasedTargetTypes);
      if (parentClassFqcn != null) {
        entry.getValue().setParentInjector(parentClassFqcn + SUFFIX);
      }
    }

    return targetClassMap;
  }

  private void findAndParseListener(RoundEnvironment env,
      Class<? extends Annotation> annotationClass, Map<TypeElement, TargetClass> targetClassMap,
      Set<TypeMirror> erasedTargetTypes) {
    for (Element element : env.getElementsAnnotatedWith(annotationClass)) {
      try {
        parseListenerAnnotation(annotationClass, element, targetClassMap, erasedTargetTypes);
      } catch (Exception e) {
        error(element, "Unable to parse @%s: %s", annotationClass.getSimpleName(), e.getMessage());
      }
    }
  }

  private void parseInjectView(Element element, Map<TypeElement, TargetClass> targetClassMap,
      Set<TypeMirror> erasedTargetTypes) {
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type extends from View.
    if (!isSubclassOfView(element.asType())) {
      error(element, "@InjectView fields must extend from View (%s.%s).",
          enclosingElement.getQualifiedName(), element);
      return;
    }

    // Verify containing type.
    if (enclosingElement.getKind() != CLASS) {
      error(element, "@InjectView field annotations may only be specified in classes (%s).",
          enclosingElement);
      return;
    }

    // Verify field modifiers.
    Set<Modifier> modifiers = element.getModifiers();
    if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
      error(element, "@InjectView fields must not be private or static (%s.%s).",
          enclosingElement.getQualifiedName(), element);
      return;
    }

    // Verify containing class visibility is not private.
    if (enclosingElement.getModifiers().contains(PRIVATE)) {
      error(element, "@InjectView fields may not be on private classes (%s).", enclosingElement);
      return;
    }

    // Assemble information on the injection point.
    String name = element.getSimpleName().toString();
    int id = element.getAnnotation(InjectView.class).value();
    String type = element.asType().toString();
    boolean required = element.getAnnotation(Optional.class) == null;

    TargetClass targetClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
    targetClass.addField(id, name, type, required);

    // Add the type-erased version to the valid injection targets set.
    TypeMirror erasedTargetType = typeUtils.erasure(enclosingElement.asType());
    erasedTargetTypes.add(erasedTargetType);
  }

  private void parseListenerAnnotation(Class<? extends Annotation> annotationClass, Element element,
      Map<TypeElement, TargetClass> targetClassMap, Set<TypeMirror> erasedTargetTypes)
      throws Exception {
    // This should be guarded by the annotation's @Target but it's worth a check for safe casting.
    if (!(element instanceof ExecutableElement) || element.getKind() != METHOD) {
      error(element, "@%s annotation must be on a method.", annotationClass.getSimpleName());
      return;
    }

    String annotationName = annotationClass.getName();
    InjectableListenerHandler handler = LISTENER_HANDLER_MAP.get(annotationName);
    if (handler == null) {
      InjectableListener listener = annotationClass.getAnnotation(InjectableListener.class);
      if (listener == null) {
        error(element, "No @%s defined on @%s.", InjectableListener.class.getSimpleName(),
            annotationClass.getSimpleName());
        return;
      }
      Class<? extends InjectableListenerHandler> handlerClass = listener.value();
      handler = handlerClass.newInstance();
      LISTENER_HANDLER_MAP.put(annotationName, handler);
    }

    ExecutableElement executableElement = (ExecutableElement) element;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Assemble information on the injection point.
    String name = executableElement.getSimpleName().toString();
    Annotation annotation = element.getAnnotation(annotationClass);
    int[] ids = (int[]) annotationClass.getDeclaredMethod("value").invoke(annotation);
    boolean required = element.getAnnotation(Optional.class) == null;

    // Verify method modifiers.
    Set<Modifier> modifiers = element.getModifiers();
    if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
      throw new InjectableListenerException(element,
          "@%s methods must not be private or static. (%s.%s)", annotationClass.getSimpleName(),
          enclosingElement.getQualifiedName(), element.getSimpleName());
    }

    // Verify containing type.
    if (enclosingElement.getKind() != CLASS) {
      throw new InjectableListenerException(enclosingElement,
          "@%s methods may only be contained in classes. (%s.%s)", annotationClass.getSimpleName(),
          enclosingElement.getQualifiedName(), element.getSimpleName());
    }

    // Verify containing class visibility is not private.
    if (enclosingElement.getModifiers().contains(PRIVATE)) {
      throw new InjectableListenerException(enclosingElement,
          "@%s methods may not be contained in private classes. (%s.%s)",
          annotationClass.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
    }

    // Verify method return type.
    if (executableElement.getReturnType().getKind() != TypeKind.VOID) {
      throw new InjectableListenerException(element,
          "@%s methods must have a 'void' return type. (%s.%s)", annotationClass.getSimpleName(),
          enclosingElement.getQualifiedName(), element.getSimpleName());
    }

    Set<Integer> seenIds = new LinkedHashSet<Integer>(ids.length);
    for (int id : ids) {
      if (!seenIds.add(id)) {
        throw new InjectableListenerException(element,
            "@%s annotation for method contains duplicate ID %d. (%s.%s)",
            annotationClass.getSimpleName(), id, enclosingElement.getQualifiedName(),
            element.getSimpleName());
      }
    }

    Param[] params;
    try {
      params = handler.parseParamTypesAndValidateMethod(this, executableElement);
    } catch (InjectableListenerException e) {
      error(e.element, e.getMessage());
      return;
    }

    TargetClass targetClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
    for (int id : ids) {
      if (!targetClass.addMethod(id, annotationName, name, params, required)) {
        error(element, "Multiple @%s methods declared for ID %s in %s.",
            annotationClass.getSimpleName(), id, enclosingElement.getQualifiedName());
        return;
      }
    }

    // Add the type-erased version to the valid injection targets set.
    TypeMirror erasedTargetType = typeUtils.erasure(enclosingElement.asType());
    erasedTargetTypes.add(erasedTargetType);
  }

  static boolean isSubclassOfView(TypeMirror typeMirror) {
    if (!(typeMirror instanceof DeclaredType)) {
      return false;
    }
    DeclaredType declaredType = (DeclaredType) typeMirror;
    if (VIEW_TYPE.equals(declaredType.toString())) {
      return true;
    }
    Element element = declaredType.asElement();
    if (!(element instanceof TypeElement)) {
      return false;
    }
    TypeElement typeElement = (TypeElement) element;
    TypeMirror superType = typeElement.getSuperclass();
    return isSubclassOfView(superType);
  }

  static boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
    if (!(typeMirror instanceof DeclaredType)) {
      return typeMirror.toString().equals(otherType);
    }
    DeclaredType declaredType = (DeclaredType) typeMirror;
    if (otherType.equals(declaredType.toString())) {
      return true;
    }
    Element element = declaredType.asElement();
    if (!(element instanceof TypeElement)) {
      return false;
    }
    TypeElement typeElement = (TypeElement) element;
    TypeMirror superType = typeElement.getSuperclass();
    if (isSubclassOfView(superType)) {
      return true;
    }
    for (TypeMirror interfaceType : typeElement.getInterfaces()) {
      if (isSubclassOfView(interfaceType)) {
        return true;
      }
    }
    return false;
  }

  private TargetClass getOrCreateTargetClass(Map<TypeElement, TargetClass> targetClassMap,
      TypeElement enclosingElement) {
    TargetClass targetClass = targetClassMap.get(enclosingElement);
    if (targetClass == null) {
      String targetType = enclosingElement.getQualifiedName().toString();
      String classPackage = getPackageName(enclosingElement);
      String className = getClassName(enclosingElement, classPackage) + SUFFIX;

      targetClass = new TargetClass(classPackage, className, targetType);
      targetClassMap.put(enclosingElement, targetClass);
    }
    return targetClass;
  }

  private static String getClassName(TypeElement type, String packageName) {
    int packageLen = packageName.length() + 1;
    return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
  }

  /** Finds the parent injector type in the supplied set, if any. */
  private String findParentFqcn(TypeElement typeElement, Set<TypeMirror> parents) {
    TypeMirror type;
    while (true) {
      type = typeElement.getSuperclass();
      if (type.getKind() == TypeKind.NONE) {
        return null;
      }
      typeElement = (TypeElement) ((DeclaredType) type).asElement();
      if (containsTypeMirror(parents, type)) {
        String packageName = getPackageName(typeElement);
        return packageName + "." + getClassName(typeElement, packageName);
      }
    }
  }

  private boolean containsTypeMirror(Collection<TypeMirror> mirrors, TypeMirror query) {
    // Ensure we are checking against a type-erased version for normalization purposes.
    query = typeUtils.erasure(query);

    for (TypeMirror mirror : mirrors) {
      if (typeUtils.isSameType(mirror, query)) {
        return true;
      }
    }
    return false;
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  void error(Element element, String message, Object... args) {
    processingEnv.getMessager().printMessage(ERROR, String.format(message, args), element);
  }

  protected String getPackageName(TypeElement type) {
    return elementUtils.getPackageOf(type).getQualifiedName().toString();
  }

  public void findBestParameter(Param[] params, List<? extends VariableElement> parameters,
      Class<?> targetClass, int listenerPosition) {
    String targetClassName = targetClass.getName();
    for (int i = 0, count = parameters.size(); i < count; i++) {
      if (params[i] != null) {
        continue;
      }
      VariableElement parameter = parameters.get(i);
      if (isSubtypeOfType(parameter.asType(), targetClassName)) {
        params[i] = new Param(listenerPosition, parameter.asType().toString());
        return;
      }
    }
  }
}
