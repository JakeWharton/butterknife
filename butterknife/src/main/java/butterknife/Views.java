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

  private static final Map<Class<?>, Method> INJECTORS = new LinkedHashMap<Class<?>, Method>();

  /**
   * Inject fields annotated with {@link InjectView} in the specified {@link Activity}.
   *
   * @param target Target activity for field injection.
   * @throws UnableToInjectException if injection could not be performed.
   */
  public static void inject(Activity target) {
    inject(target, Activity.class, target);
  }

  /**
   * Inject fields annotated with {@link InjectView} in the specified {@code source} using {@code
   * target} as the view root.
   *
   * @param target Target class for field injection.
   * @param source View tree root on which IDs will be looked up.
   * @throws UnableToInjectException if injection could not be performed.
   */
  public static void inject(Object target, View source) {
    inject(target, View.class, source);
  }

  private static void inject(Object target, Class<?> sourceType, Object source) {
    try {
      Class<?> targetClass = target.getClass();
      Method inject = INJECTORS.get(targetClass);
      if (inject == null) {
        Class<?> injector = Class.forName(targetClass.getName() + AnnotationProcessor.SUFFIX);
        inject = injector.getMethod("inject", targetClass, sourceType);
        INJECTORS.put(targetClass, inject);
      }
      inject.invoke(null, target, source);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnableToInjectException("Unable to inject views for " + target, e);
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

  public static class UnableToInjectException extends RuntimeException {
    UnableToInjectException(String message, Throwable cause) {
      super(message, cause);
    }
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
      TypeMirror viewType = processingEnv.getElementUtils().getTypeElement(TYPE_VIEW).asType();

      Map<TypeElement, Set<InjectionPoint>> injectionsByClass =
          new LinkedHashMap<TypeElement, Set<InjectionPoint>>();
      Set<TypeMirror> injectionTargets = new HashSet<TypeMirror>();

      for (Element element : env.getElementsAnnotatedWith(InjectView.class)) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target type extends from View.
        if (!processingEnv.getTypeUtils().isSubtype(element.asType(), viewType)) {
          error("@InjectView fields must extend from View (%s.%s).",
              enclosingElement.getQualifiedName(), element);
          continue;
        }

        // Verify field properties.
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(PRIVATE) || modifiers.contains(PROTECTED) || modifiers.contains(
            STATIC)) {
          error("@InjectView fields must not be private, protected, or static (%s.%s).",
              enclosingElement.getQualifiedName(), element);
          continue;
        }

        // Verify containing type.
        if (enclosingElement.getKind() != CLASS) {
          error("@InjectView field annotations may only be specified in classes (%s).", element);
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
        Set<InjectionPoint> injectionPoints = injection.getValue();

        String targetType = type.getQualifiedName().toString();
        String sourceType = resolveSourceType(type);
        String packageName = processingEnv.getElementUtils().getPackageOf(type).toString();
        String className =
            type.getQualifiedName().toString().substring(packageName.length() + 1).replace('.', '$')
                + SUFFIX;
        String parentClass = resolveParentType(type, injectionTargets);
        StringBuilder injections = new StringBuilder();
        if (parentClass != null) {
          injections.append("    ")
              .append(parentClass)
              .append(SUFFIX)
              .append(".inject(activity);\n\n");
        }
        for (InjectionPoint injectionPoint : injectionPoints) {
          injections.append(injectionPoint).append("\n");
        }

        // Write the view injector class.
        try {
          JavaFileObject jfo =
              processingEnv.getFiler().createSourceFile(packageName + "." + className, type);
          Writer writer = jfo.openWriter();
          writer.write(String.format(INJECTOR, packageName, className, targetType, sourceType,
              injections.toString()));
          writer.flush();
          writer.close();
        } catch (IOException e) {
          error(e.getMessage());
        }
      }

      return true;
    }

    /** Returns {@link #TYPE_ACTIVITY} or {@link #TYPE_VIEW} as the injection target type. */
    private String resolveSourceType(TypeElement typeElement) {
      TypeMirror type;
      while (true) {
        type = typeElement.getSuperclass();
        if (type.getKind() == TypeKind.NONE) {
          return TYPE_VIEW;
        }
        if (type.toString().equals(TYPE_ACTIVITY)) {
          return TYPE_ACTIVITY;
        }
        typeElement = (TypeElement) ((DeclaredType) type).asElement();
      }
    }

    /** Finds the parent injector type in the supplied set, if any. */
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

    private static final String TYPE_ACTIVITY = "android.app.Activity";
    private static final String TYPE_VIEW = "android.view.View";
    private static final String INJECTION = "    target.%s = (%s) source.findViewById(%s);";
    private static final String INJECTOR = ""
        + "package %s;\n\n"
        + "public class %s {\n"
        + "  public static void inject(%s target, %s source) {\n"
        + "%s"
        + "  }\n"
        + "}\n";
  }
}
