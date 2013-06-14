package butterknife;

import android.app.Activity;
import android.view.View;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
      @Override public View findById(Object source, int id) {
        return ((View) source).findViewById(id);
      }
    },
    ACTIVITY {
      @Override public View findById(Object source, int id) {
        return ((Activity) source).findViewById(id);
      }
    };

    public abstract View findById(Object source, int id);
  }

  static final Map<Class<?>, Method> INJECTORS = new LinkedHashMap<Class<?>, Method>();
  static final Map<Class<?>, Method> RESETTERS = new LinkedHashMap<Class<?>, Method>();
  static final Method NO_OP = null;

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

  /**
   * Reset fields annotated with {@link InjectView} to {@code null}.
   * <p>
   * This should only be used in the {@code onDestroyView} method of a fragment in practice.
   *
   * @param target Target class for field reset.
   * @throws UnableToResetException if views could not be reset.
   */
  public static void reset(Object target) {
    Class<?> targetClass = target.getClass();
    try {
      Method reset = findResettersForClass(targetClass);
      if (reset != null) {
        reset.invoke(null, target);
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnableToResetException("Unable to reset views for " + target, e);
    }
  }

  static void inject(Object target, Object source, Finder finder) {
    Class<?> targetClass = target.getClass();
    try {
      Method inject = findInjectorForClass(targetClass);
      if (inject != null) {
        inject.invoke(null, finder, target, source);
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnableToInjectException("Unable to inject views for " + target, e);
    }
  }

  static Method findInjectorForClass(Class<?> cls) throws NoSuchMethodException {
    Method inject = INJECTORS.get(cls);
    if (inject != null) {
      return inject;
    }
    String clsName = cls.getName();
    if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
      return NO_OP;
    }
    try {
      Class<?> injector = Class.forName(clsName + InjectViewProcessor.SUFFIX);
      inject = injector.getMethod("inject", Finder.class, cls, Object.class);
    } catch (ClassNotFoundException e) {
      inject = findInjectorForClass(cls.getSuperclass());
    }
    INJECTORS.put(cls, inject);
    return inject;
  }

  static Method findResettersForClass(Class<?> cls) throws NoSuchMethodException {
    Method inject = RESETTERS.get(cls);
    if (inject != null) {
      return inject;
    }
    String clsName = cls.getName();
    if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
      return NO_OP;
    }
    try {
      Class<?> injector = Class.forName(clsName + InjectViewProcessor.SUFFIX);
      inject = injector.getMethod("reset", cls);
    } catch (ClassNotFoundException e) {
      inject = findResettersForClass(cls.getSuperclass());
    }
    RESETTERS.put(cls, inject);
    return inject;
  }

  /** Simpler version of {@link View#findViewById(int)} which infers the target type. */
  @SuppressWarnings({ "unchecked", "UnusedDeclaration" }) // Checked by runtime cast. Public API.
  public static <T extends View> T findById(View view, int id) {
    return (T) view.findViewById(id);
  }

  /** Simpler version of {@link Activity#findViewById(int)} which infers the target type. */
  @SuppressWarnings({ "unchecked", "UnusedDeclaration" }) // Checked by runtime cast. Public API.
  public static <T extends View> T findById(Activity activity, int id) {
    return (T) activity.findViewById(id);
  }

  public static class UnableToInjectException extends RuntimeException {
    UnableToInjectException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class UnableToResetException extends RuntimeException {
    UnableToResetException(String message, Throwable cause) {
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

      Map<TypeElement, Map<Integer, Set<InjectionPoint>>> injectionsByClass =
          new LinkedHashMap<TypeElement, Map<Integer, Set<InjectionPoint>>>();
      Set<TypeMirror> injectionTargets = new LinkedHashSet<TypeMirror>();

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
        Map<Integer, Set<InjectionPoint>> injections = injectionsByClass.get(enclosingElement);
        if (injections == null) {
          injections = new LinkedHashMap<Integer, Set<InjectionPoint>>();
          injectionsByClass.put(enclosingElement, injections);
        }

        // Assemble information on the injection point.
        String variableName = element.getSimpleName().toString();
        int value = element.getAnnotation(InjectView.class).value();
        String type = element.asType().toString();

        Set<InjectionPoint> injectionPoints = injections.get(value);
        if (injectionPoints == null) {
          injectionPoints = new LinkedHashSet<InjectionPoint>();
          injections.put(value, injectionPoints);
        }

        injectionPoints.add(new InjectionPoint(variableName, type));

        // Add the type-erased version to the valid injection targets set.
        TypeMirror erasedInjectionType = typeUtils.erasure(enclosingElement.asType());
        injectionTargets.add(erasedInjectionType);
      }

      for (Map.Entry<TypeElement, Map<Integer, Set<InjectionPoint>>> injection //
          : injectionsByClass.entrySet()) {
        TypeElement type = injection.getKey();
        String targetType = type.getQualifiedName().toString();
        String classPackage = getPackageName(type);
        String className = getClassName(type, classPackage) + SUFFIX;
        String classFqcn = classPackage + "." + className;
        String parentClassFqcn = findParentFqcn(type, injectionTargets);
        StringBuilder injectBuilder = new StringBuilder();
        StringBuilder resetBuilder = new StringBuilder();
        if (parentClassFqcn != null) {
          injectBuilder.append(String.format(PARENT_INJECT, parentClassFqcn, SUFFIX)).append('\n');
          resetBuilder.append(String.format(PARENT_RESET, parentClassFqcn, SUFFIX)).append('\n');
        }
        for (Map.Entry<Integer, Set<InjectionPoint>> viewIdInjections : injection.getValue()
            .entrySet()) {
          injectBuilder.append(String.format(FINDER, viewIdInjections.getKey())).append('\n');
          for (InjectionPoint injectionPoint : viewIdInjections.getValue()) {
            injectBuilder.append(injectionPoint).append('\n');
            resetBuilder.append(String.format(RESET, injectionPoint.variableName)).append('\n');
          }
        }
        String injections = injectBuilder.toString();
        String resetters = resetBuilder.toString();

        // Write the view injector class.
        try {
          JavaFileObject jfo = filer.createSourceFile(classFqcn, type);
          Writer writer = jfo.openWriter();
          writer.write(
              String.format(INJECTOR, classPackage, className, targetType, injections, targetType,
                  resetters));
          writer.flush();
          writer.close();
        } catch (IOException e) {
          error(type, "Unable to write injector for type %s: %s", type, e.getMessage());
        }
      }

      return true;
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
      Types typeUtils = processingEnv.getTypeUtils();

      // Ensure we are checking against a type-erased version for normalization purposes.
      query = typeUtils.erasure(query);

      for (TypeMirror mirror : mirrors) {
        if (typeUtils.isSameType(mirror, query)) {
          return true;
        }
      }
      return false;
    }

    private String getPackageName(TypeElement type) {
      return processingEnv.getElementUtils().getPackageOf(type).getQualifiedName().toString();
    }

    private static String getClassName(TypeElement type, String packageName) {
      return type.getQualifiedName()
          .toString()
          .substring(packageName.length() + 1)
          .replace('.', '$');
    }

    private static class InjectionPoint {
      private final String variableName;
      private final String type;

      InjectionPoint(String variableName, String type) {
        this.variableName = variableName;
        this.type = type;
      }

      @Override public String toString() {
        return String.format(INJECT, variableName, type);
      }
    }

    private static final String FINDER = "    view = finder.findById(source, %s);";
    private static final String INJECT = "    target.%s = (%s) view;";
    private static final String RESET = "    target.%s = null;";
    private static final String PARENT_INJECT = "    %s%s.inject(finder, target, source);";
    private static final String PARENT_RESET = "    %s%s.reset(target);";
    private static final String INJECTOR = ""
        + "// Generated code from Butter Knife. Do not modify!\n"
        + "package %s;\n\n"
        + "import android.view.View;\n"
        + "import butterknife.Views.Finder;\n\n"
        + "public class %s {\n"
        + "  public static void inject(Finder finder, %s target, Object source) {\n"
        + "    View view;\n"
        + "%s"
        + "  }\n\n"
        + "  public static void reset(%s target) {\n"
        + "%s"
        + "  }\n"
        + "}\n";
  }
}
