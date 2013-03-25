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
import javax.annotation.processing.Filer;
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

/** View injection utilities. */
public class Views {
  private Views() {
    // No instances.
  }

  public enum Finder {
    VIEW {
      @SuppressWarnings("unchecked") @Override
      public <T extends View> T findById(Object source, int id) {
        return (T) ((View) source).findViewById(id);
      }
    },
    ACTIVITY {
      @SuppressWarnings("unchecked") @Override
      public <T extends View> T findById(Object source, int id) {
        return (T) ((Activity) source).findViewById(id);
      }
    };

    public abstract <T extends View> T findById(Object source, int id);
  }

  private static final Map<Class<?>, Method> INJECTORS = new LinkedHashMap<Class<?>, Method>();
  private static final Method NO_OP = null;

  /**
   * Inject fields annotated with {@link InjectView} in the specified {@link Activity}. The current
   * content view is used as the view root.
   *
   * @param target Target activity for field injection.
   * @throws UnableToInjectException if injection could not be performed.
   */
  public static void inject(Activity target) {
    inject(target, target, Finder.ACTIVITY);
  }

  /**
   * Inject fields annotated with {@link InjectView} in the specified {@link View}. The view and
   * its children are used as the view root.
   *
   * @param target Target view for field injection.
   * @throws UnableToInjectException if injection could not be performed.
   */
  public static void inject(View target) {
    inject(target, target, Finder.VIEW);
  }

  /**
   * Inject fields annotated with {@link InjectView} in the specified {@code source} using the
   * {@code target} {@link Activity} as the view root.
   *
   * @param target Target class for field injection.
   * @param source Activity on which IDs will be looked up.
   * @throws UnableToInjectException if injection could not be performed.
   */
  public static void inject(Object target, Activity source) {
    inject(target, source, Finder.ACTIVITY);
  }

  /**
   * Inject fields annotated with {@link InjectView} in the specified {@code source} using the
   * {@code target} {@link View} as the view root.
   *
   * @param target Target class for field injection.
   * @param source View root on which IDs will be looked up.
   * @throws UnableToInjectException if injection could not be performed.
   */
  public static void inject(Object target, View source) {
    inject(target, source, Finder.VIEW);
  }

  static void inject(Object target, Object source, Finder finder) {
    Class<?> targetClass = target.getClass();
    try {
      Method inject;
      if (!INJECTORS.containsKey(targetClass)) {
        Class<?> injector = Class.forName(targetClass.getName() + InjectViewProcessor.SUFFIX);
        inject = injector.getMethod("inject", Finder.class, targetClass, Object.class);
        INJECTORS.put(targetClass, inject);
      } else {
        inject = INJECTORS.get(targetClass);
      }
      // Allows for no-ops when there's nothing to inject.
      if (inject != null) {
        inject.invoke(null, finder, target, source);
      }
    } catch (ClassNotFoundException e) {
      // Allows inject to be called on targets without injected Views.
      INJECTORS.put(targetClass, NO_OP);
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
  public static class InjectViewProcessor extends AbstractProcessor {
    static final String SUFFIX = "$$ViewInjector";

    @Override public SourceVersion getSupportedSourceVersion() {
      return SourceVersion.latestSupported();
    }

    private void error(Element element, String message, Object... args) {
      processingEnv.getMessager().printMessage(ERROR, String.format(message, args), element);
    }

    @Override public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
      Elements elementUtils = processingEnv.getElementUtils();
      Types typeUtils = processingEnv.getTypeUtils();
      Filer filer = processingEnv.getFiler();

      TypeMirror viewType = elementUtils.getTypeElement("android.view.View").asType();

      Map<TypeElement, Set<InjectionPoint>> injectionsByClass =
          new LinkedHashMap<TypeElement, Set<InjectionPoint>>();
      Set<TypeMirror> injectionTargets = new HashSet<TypeMirror>();

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
          error(element, "@InjectView fields may not be on private classes (%s).",
              enclosingElement);
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
        int value = element.getAnnotation(InjectView.class).value();
        injections.add(new InjectionPoint(variableName, value));

        // Add to the valid injection targets set.
        injectionTargets.add(enclosingElement.asType());
      }

      for (Map.Entry<TypeElement, Set<InjectionPoint>> injection : injectionsByClass.entrySet()) {
        TypeElement type = injection.getKey();
        Set<InjectionPoint> injectionPoints = injection.getValue();

        String targetType = type.getQualifiedName().toString();
        String packageName = elementUtils.getPackageOf(type).getQualifiedName().toString();
        String className =
            type.getQualifiedName().toString().substring(packageName.length() + 1).replace('.', '$')
                + SUFFIX;
        String parentClass = resolveParentType(type, injectionTargets);
        StringBuilder injections = new StringBuilder();
        if (parentClass != null) {
          injections.append(String.format(PARENT, parentClass)).append('\n');
        }
        for (InjectionPoint injectionPoint : injectionPoints) {
          injections.append(injectionPoint).append('\n');
        }

        // Write the view injector class.
        try {
          JavaFileObject jfo = filer.createSourceFile(packageName + "." + className, type);
          Writer writer = jfo.openWriter();
          writer.write(
              String.format(INJECTOR, packageName, className, targetType, injections.toString()));
          writer.flush();
          writer.close();
        } catch (IOException e) {
          error(type, "Unable to write injector for type %s: %s", type, e.getMessage());
        }
      }

      return true;
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
      private final int value;

      InjectionPoint(String variableName, int value) {
        this.variableName = variableName;
        this.value = value;
      }

      @Override public String toString() {
        return String.format(INJECTION, variableName, value);
      }
    }

    private static final String INJECTION = "    target.%s = finder.findById(source, %s);";
    private static final String PARENT = "    %s.inject(finder, target, source);";
    private static final String INJECTOR = ""
        + "// Generated code from Butter Knife. Do not modify!\n"
        + "package %s;\n\n"
        + "import butterknife.Views.Finder;\n\n"
        + "public class %s {\n"
        + "  public static void inject(Finder finder, %s target, Object source) {\n"
        + "%s"
        + "  }\n"
        + "}\n";
  }
}
