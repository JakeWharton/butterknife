package butterknife.internal;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Optional;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;

@SupportedAnnotationTypes({ //
    "butterknife.InjectView", //
    "butterknife.OnClick", //
	"butterknife.OnLongClick", //
})
public class InjectViewProcessor extends AbstractProcessor {
  public static final String SUFFIX = "$$ViewInjector";

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

    // Process each @OnClick elements.
    for (Element element : env.getElementsAnnotatedWith(OnClick.class)) {
      try {
        parseOnClick(element, targetClassMap, erasedTargetTypes);
      } catch (Exception e) {
        error(element, "Unable to parse @OnClick: %s", e.getMessage());
      }
    }

	// Process each @OnLongClick elements.
	for (Element element : env.getElementsAnnotatedWith(OnLongClick.class)) {
		try {
			parseOnLongClick(element, targetClassMap, erasedTargetTypes);
		} catch (Exception e) {
			error(element, "Unable to parse @OnLongClick %s", e.getMessage());
		}
	}

    // Try to find a parent injector for each injector.
    for (Map.Entry<TypeElement, TargetClass> entry : targetClassMap.entrySet()) {
      String parentClassFqcn = findParentFqcn(entry.getKey(), erasedTargetTypes);
      if (parentClassFqcn != null) {
        entry.getValue().setParentInjector(parentClassFqcn + SUFFIX);
      }
    }

    return targetClassMap;
  }

  private void parseInjectView(Element element, Map<TypeElement, TargetClass> targetClassMap,
      Set<TypeMirror> erasedTargetTypes) {
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type extends from View.
    if (!isSubtypeOfView(element.asType())) {
      error(element, "@InjectView fields must extend from View (%s.%s).",
          enclosingElement.getQualifiedName(), element);
      return;
    }

    // Verify field modifiers.
    Set<Modifier> modifiers = element.getModifiers();
    if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
      error(element, "@InjectView fields must not be private or static (%s.%s).",
          enclosingElement.getQualifiedName(), element);
      return;
    }

    // Verify containing type.
    if (enclosingElement.getKind() != CLASS) {
      error(element, "@InjectView field annotations may only be specified in classes (%s).",
          enclosingElement);
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

  private void parseOnClick(Element element, Map<TypeElement, TargetClass> targetClassMap,
      Set<TypeMirror> erasedTargetTypes) {
    if (!(element instanceof ExecutableElement)) {
      error(element, "@OnClick annotation must be on a method.");
      return;
    }

    ExecutableElement executableElement = (ExecutableElement) element;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify method modifiers.
    Set<Modifier> modifiers = element.getModifiers();
    if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
      error(element, "@OnClick methods must not be private or static (%s.%s).",
          enclosingElement.getQualifiedName(), element);
      return;
    }

    // Verify containing type.
    if (enclosingElement.getKind() != CLASS) {
      error(element, "@OnClick method annotations may only be specified in classes (%s).",
          enclosingElement);
      return;
    }

    // Verify containing class visibility is not private.
    if (enclosingElement.getModifiers().contains(PRIVATE)) {
      error(element, "@OnClick methods may not be on private classes (%s).", enclosingElement);
      return;
    }

    // Verify method return type.
    if (executableElement.getReturnType().getKind() != TypeKind.VOID) {
      error(element, "@OnClick methods must have a 'void' return type (%s.%s).",
          enclosingElement.getQualifiedName(), element);
      return;
    }

    String type = null;
    List<? extends VariableElement> parameters = executableElement.getParameters();
    if (!parameters.isEmpty()) {
      // Verify that there is only a single parameter.
      if (parameters.size() != 1) {
        error(element,
            "@OnClick methods may only have one parameter which is View (or subclass) (%s.%s).",
            enclosingElement.getQualifiedName(), element);
        return;
      }
      // Verify that the parameter type extends from View.
      VariableElement variableElement = parameters.get(0);
      if (!isSubtypeOfView(variableElement.asType())) {
        error(element, "@OnClick method parameter must extend from View (%s.%s).",
            enclosingElement.getQualifiedName(), element);
        return;
      }

      type = variableElement.asType().toString();
    }

    // Assemble information on the injection point.
    String name = executableElement.getSimpleName().toString();
    int[] ids = element.getAnnotation(OnClick.class).value();
    boolean required = element.getAnnotation(Optional.class) == null;

    TargetClass targetClass = getOrCreateTargetClass(targetClassMap, enclosingElement);

    Set<Integer> seenIds = new LinkedHashSet<Integer>();
    for (int id : ids) {
      if (!seenIds.add(id)) {
        error(element, "@OnClick annotation for method %s contains duplicate ID %s.", element,
            id);
        return;
      } else if (!targetClass.addMethod(id, name, type, required, MethodInjectionType.OnClick)) {
        error(element, "Multiple @OnClick methods declared for ID %s in %s.", id,
            enclosingElement.getQualifiedName());
        return;
      }
    }

    // Add the type-erased version to the valid injection targets set.
    TypeMirror erasedTargetType = typeUtils.erasure(enclosingElement.asType());
    erasedTargetTypes.add(erasedTargetType);
  }

  private void parseOnLongClick(Element element, Map<TypeElement, TargetClass> targetClassMap,
      Set<TypeMirror> erasedTargetTypes) {
    if (!(element instanceof ExecutableElement)) {
      error(element, "@OnLongClick annotation must be on a method.");
      return;
    }

    ExecutableElement executableElement = (ExecutableElement) element;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify method modifiers.
    Set<Modifier> modifiers = element.getModifiers();
    if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
      error(element, "@OnLongClick methods must not be private or static (%s.%s).",
          enclosingElement.getQualifiedName(), element);
      return;
    }

    // Verify containing type.
    if (enclosingElement.getKind() != CLASS) {
      error(element, "@OnLongClick method annotations may only be specified in classes (%s).",
          enclosingElement);
      return;
    }

    // Verify containing class visibility is not private.
    if (enclosingElement.getModifiers().contains(PRIVATE)) {
      error(element, "@OnLongClick methods may not be on private classes (%s).", enclosingElement);
      return;
    }

    // Verify method return type.
    if (executableElement.getReturnType().getKind() != TypeKind.BOOLEAN) {
      error(element, "@OnLongClick methods must have a 'boolean' return type (%s.%s).",
          enclosingElement.getQualifiedName(), element);
      return;
    }

    String type = null;
    List<? extends VariableElement> parameters = executableElement.getParameters();
    if (!parameters.isEmpty()) {
      // Verify that there is only a single parameter.
      if (parameters.size() != 1) {
        error(element,
            "@OnLongClick methods may only have one parameter which is View (or subclass) (%s.%s).",
            enclosingElement.getQualifiedName(), element);
        return;
      }
      // Verify that the parameter type extends from View.
      VariableElement variableElement = parameters.get(0);
      if (!isSubtypeOfView(variableElement.asType())) {
        error(element, "@OnLongClick method parameter must extend from View (%s.%s).",
            enclosingElement.getQualifiedName(), element);
        return;
      }

      type = variableElement.asType().toString();
    }

    // Assemble information on the injection point.
    String name = executableElement.getSimpleName().toString();
    int[] ids = element.getAnnotation(OnLongClick.class).value();
    boolean required = element.getAnnotation(Optional.class) == null;

    TargetClass targetClass = getOrCreateTargetClass(targetClassMap, enclosingElement);

    Set<Integer> seenIds = new LinkedHashSet<Integer>();
    for (int id : ids) {
      if (!seenIds.add(id)) {
        error(element, "@OnLongClick annotation for method %s contains duplicate ID %s.", element,
            id);
        return;
      } else if (!targetClass.addMethod(id, name, type, required, MethodInjectionType.OnLongClick)) {
        error(element, "Multiple @OnLongClick methods declared for ID %s in %s.", id,
            enclosingElement.getQualifiedName());
        return;
      }
    }

    // Add the type-erased version to the valid injection targets set.
    TypeMirror erasedTargetType = typeUtils.erasure(enclosingElement.asType());
    erasedTargetTypes.add(erasedTargetType);
  }

  private boolean isSubtypeOfView(TypeMirror typeMirror) {
    if (!(typeMirror instanceof DeclaredType)) {
      return false;
    }
    DeclaredType declaredType = (DeclaredType) typeMirror;
    if ("android.view.View".equals(declaredType.toString())) {
      return true;
    } else {
      Element element = declaredType.asElement();
      if (!(element instanceof TypeElement)) {
        return false;
      }
      TypeElement typeElement = (TypeElement) element;
      TypeMirror superType = typeElement.getSuperclass();
      return isSubtypeOfView(superType);
    }
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

  protected static String getClassName(TypeElement type, String packageName) {
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

  protected void error(Element element, String message, Object... args) {
    processingEnv.getMessager().printMessage(ERROR, String.format(message, args), element);
  }

  protected String getPackageName(TypeElement type) {
    return elementUtils.getPackageOf(type).getQualifiedName().toString();
  }
}
