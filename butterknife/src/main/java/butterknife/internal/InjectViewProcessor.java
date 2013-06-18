package butterknife.internal;

import butterknife.InjectView;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;

@SupportedAnnotationTypes("butterknife.InjectView")
public class InjectViewProcessor extends AbstractProcessor {
  public static final String SUFFIX = "$$ViewInjector";

  private Elements elementUtils;
  private Types typeUtils;
  private Filer filer;

  private TypeMirror viewType;

  @Override public synchronized void init(ProcessingEnvironment env) {
    super.init(env);

    elementUtils = env.getElementUtils();
    typeUtils = env.getTypeUtils();
    filer = env.getFiler();

    viewType = elementUtils.getTypeElement("android.view.View").asType();
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

    for (Element element : env.getElementsAnnotatedWith(InjectView.class)) {
      TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

      // Verify that the target type extends from View.
      if (!typeUtils.isSubtype(element.asType(), viewType)) {
        error(element, "@InjectView fields must extend from View (%s.%s).",
            enclosingElement.getQualifiedName(), element);
        continue;
      }

      // Verify field properties.
      Set<Modifier> modifiers = element.getModifiers();
      if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
        error(element, "@InjectView fields must not be private or static (%s.%s).",
            enclosingElement.getQualifiedName(), element);
        continue;
      }

      // Verify containing type.
      if (enclosingElement.getKind() != CLASS) {
        error(element, "@InjectView field annotations may only be specified in classes (%s).",
            enclosingElement);
        continue;
      }

      // Verify containing class visibility is not private.
      if (enclosingElement.getModifiers().contains(PRIVATE)) {
        error(element, "@InjectView fields may not be on private classes (%s).", enclosingElement);
        continue;
      }

      TargetClass targetClass = targetClassMap.get(enclosingElement);
      if (targetClass == null) {
        String targetType = enclosingElement.getQualifiedName().toString();
        String classPackage = getPackageName(enclosingElement);
        String className = getClassName(enclosingElement, classPackage) + SUFFIX;

        targetClass = new TargetClass(classPackage, className, targetType);
        targetClassMap.put(enclosingElement, targetClass);
      }

      // Assemble information on the injection point.
      String name = element.getSimpleName().toString();
      int id = element.getAnnotation(InjectView.class).value();
      String type = element.asType().toString();

      targetClass.addField(id, name, type);

      // Add the type-erased version to the valid injection targets set.
      TypeMirror erasedTargetType = typeUtils.erasure(enclosingElement.asType());
      erasedTargetTypes.add(erasedTargetType);
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
