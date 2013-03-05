package butterknife;

import android.app.Activity;
import android.view.View;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;

public class Views {
  private Views() {
    // No instances.
  }

  /** Inject the specified {@link Activity} using the injector generated at compile-time. */
  public static void inject(Activity activity) {
    try {
      Class<?> injector = Class.forName(activity.getClass().getName() + AnnotationProcessor.SUFFIX);
      Method inject = injector.getMethod("inject", activity.getClass());
      inject.invoke(null, activity);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to inject views for activity " + activity, e);
    }
  }

  /** Simpler version of {@link View#findViewById(int)} which infers the target type. */
  @SuppressWarnings({ "unchecked", "UnusedDeclaration" }) // Checked by runtime cast, helper method.
  public static <T extends View> T findById(View view, int id) {
    return (T) view.findViewById(id);
  }

  /** Simpler version of {@link Activity#findViewById(int)} which infers the target type. */
  @SuppressWarnings({ "unchecked", "UnusedDeclaration" }) // Checked by runtime cast, helper method.
  public static <T extends View> T findById(Activity activity, int id) {
    return (T) activity.findViewById(id);
  }

  @SupportedAnnotationTypes("butterknife.InjectView")
  public static class AnnotationProcessor extends AbstractProcessor {
    static final String SUFFIX = "$$ViewInjector";

    @Override public SourceVersion getSupportedSourceVersion() {
      return SourceVersion.latestSupported();
    }

    private void error(String message, Object... args) {
      processingEnv.getMessager().printMessage(ERROR, String.format(message, args));
    }

    @Override public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
      Map<TypeElement, Set<InjectionPoint>> injectionsByClass =
          new LinkedHashMap<TypeElement, Set<InjectionPoint>>();
      Set<TypeMirror> injectionTargets = new HashSet<TypeMirror>();

      for (Element element : env.getElementsAnnotatedWith(InjectView.class)) {
        // Verify containing type.
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        if (enclosingElement.getKind() != CLASS) {
          error("Unexpected @InjectView on field in " + element);
          continue;
        }

        // Verify field properties.
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(PRIVATE) || modifiers.contains(PROTECTED) || modifiers.contains(
            STATIC)) {
          error("@InjectView fields must not be private, protected, or static: "
              + enclosingElement.getQualifiedName()
              + "."
              + element);
          continue;
        }

        // Get and optionally create a set of all injection points for a type.
        Set<InjectionPoint> injections = injectionsByClass.get(enclosingElement);
        if (injections == null) {
          injections = new HashSet<InjectionPoint>();
          injectionsByClass.put(enclosingElement, injections);
        }

        // Assemble information on the injection point.
        String variableName = element.getSimpleName().toString();
        String type = element.asType().toString();
        int value = element.getAnnotation(InjectView.class).value();
        injections.add(new InjectionPoint(variableName, type, value));

        // Add to the valid injection targets set.
        injectionTargets.add(enclosingElement.asType());
      }

      for (Map.Entry<TypeElement, Set<InjectionPoint>> injection : injectionsByClass.entrySet()) {
        TypeElement type = injection.getKey();
        String targetClass = type.getQualifiedName().toString();
        int lastDot = targetClass.lastIndexOf(".");
        String activityType = targetClass.substring(lastDot + 1);
        String className = activityType + SUFFIX;
        String packageName = targetClass.substring(0, lastDot);
        String parentClass = resolveParentType(type, injectionTargets);
        StringBuilder injections = new StringBuilder();
        if (parentClass != null) {
          injections.append("    ")
              .append(parentClass)
              .append(SUFFIX)
              .append(".inject(activity);\n\n");
        }
        for (InjectionPoint injectionPoint : injection.getValue()) {
          injections.append(injectionPoint).append("\n");
        }

        // Write the view injector class.
        try {
          JavaFileObject jfo =
              processingEnv.getFiler().createSourceFile(packageName + "." + className, type);
          Writer writer = jfo.openWriter();
          writer.write(
              String.format(INJECTOR, packageName, className, activityType, injections.toString()));
          writer.flush();
          writer.close();
        } catch (IOException e) {
          error(e.getMessage());
        }
      }

      return true;
    }

    private String resolveParentType(TypeElement typeElement, Set<TypeMirror> parents) {
      TypeMirror type;
      while (true) {
        type = typeElement.getSuperclass();
        if (type.getKind() == TypeKind.NONE) {
          return null;
        }
        if (parents.contains(type)) {
          return type.toString();
        }
        typeElement = (TypeElement) ((DeclaredType) type).asElement();
      }
    }

    private static class InjectionPoint {
      private final String variableName;
      private final String type;
      private final int value;

      InjectionPoint(String variableName, String type, int value) {
        this.variableName = variableName;
        this.type = type;
        this.value = value;
      }

      @Override public String toString() {
        return String.format(INJECTION, variableName, type, value);
      }
    }

    private static final String INJECTION = "    activity.%s = (%s) activity.findViewById(%s);";
    private static final String INJECTOR = ""
        + "package %s;\n\n"
        + "public class %s {\n"
        + "  public static void inject(%s activity) {\n"
        + "%s"
        + "  }\n"
        + "}\n";
  }
}
