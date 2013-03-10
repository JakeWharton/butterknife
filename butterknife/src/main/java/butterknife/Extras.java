package butterknife;

import android.app.Activity;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static javax.lang.model.element.Modifier.*;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * User: Nicolas PICON
 * Date: 09/03/13 - 01:32
 */
public class Extras {
  private Extras() {
    // No instances.
  }

  public static class Finder {
    @SuppressWarnings("unchecked")
    public Object getExtra(Activity target, String name) throws MissingExtraException {
      android.os.Bundle extras = target.getIntent().getExtras();
      if (extras.containsKey(name)) {
        return extras.get(name);
      } else {
        throw new MissingExtraException(
            String.format("No value found for extra %s.%s", target.getClass().getSimpleName(), name)
        );
      }
    }

    public boolean hasExtra(Activity target, String name) {
      return target.getIntent().hasExtra(name);
    }
  }

  private static final Map<Class<?>, Method> INJECTORS = new LinkedHashMap<Class<?>, Method>();

  /**
   * Inject fields annotated with {@link InjectExtra} in the specified {@link android.app.Activity}.
   *
   * @param target Target activity for field injection.
   * @throws UnableToInjectException if injection could not be performed.
   */
  public static void inject(Activity target) {
    try {
      Class<?> targetClass = target.getClass();
      Method inject = INJECTORS.get(targetClass);
      if (inject == null) {
        Class<?> injector = Class.forName(targetClass.getName() + InjectExtraProcessor.SUFFIX);
        inject = injector.getMethod("inject", Finder.class, targetClass);
        INJECTORS.put(targetClass, inject);
      }
      inject.invoke(null, new Finder(), target);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnableToInjectException("Unable to inject views for " + target, e);
    }
  }

  @SupportedAnnotationTypes("butterknife.InjectExtra")
  public static class InjectExtraProcessor extends AbstractProcessor {
    static final String SUFFIX = "$$ExtraInjector";

    @Override public SourceVersion getSupportedSourceVersion() {
      return SourceVersion.latestSupported();
    }

    private void error(Element element, String message, Object... args) {
      processingEnv.getMessager().printMessage(ERROR, String.format(message, args), element);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
      Elements elementUtils = processingEnv.getElementUtils();
      Types typeUtils = processingEnv.getTypeUtils();
      Filer filer = processingEnv.getFiler();

      TypeMirror activityType = elementUtils.getTypeElement("android.app.Activity").asType();

      // Primitives wrappers
      TypeMirror booleanType = elementUtils.getTypeElement("java.lang.Boolean").asType();
      TypeMirror byteType = elementUtils.getTypeElement("java.lang.Byte").asType();
      TypeMirror shortType = elementUtils.getTypeElement("java.lang.Short").asType();
      TypeMirror integerType = elementUtils.getTypeElement("java.lang.Integer").asType();
      TypeMirror longType = elementUtils.getTypeElement("java.lang.Long").asType();
      TypeMirror characterType = elementUtils.getTypeElement("java.lang.Character").asType();
      TypeMirror floatType = elementUtils.getTypeElement("java.lang.Float").asType();
      TypeMirror doubleType = elementUtils.getTypeElement("java.lang.Double").asType();


      Map<TypeElement, Set<InjectionPoint>> injectionsByClass =
          new LinkedHashMap<TypeElement, Set<InjectionPoint>>();

      for (Element element : env.getElementsAnnotatedWith(InjectExtra.class)) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Check enclosing element extends Activity
        if (!typeUtils.isSubtype(enclosingElement.asType(), activityType)) {
          error(element, "@InjectExtra can only be used in a class extending Activity (%s.%s).",
              enclosingElement.getQualifiedName(), element);
          continue;
        }

        // Verify field properties.
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(PRIVATE) || modifiers.contains(PROTECTED) || modifiers.contains(
            STATIC)) {
          error(element, "@InjectExtra fields must not be private, protected, or static (%s.%s).",
              enclosingElement.getQualifiedName(), element);
          continue;
        }

        // Get and optionally create a set of all injection points for a type.
        Set<InjectionPoint> injections = injectionsByClass.get(enclosingElement);
        if (injections == null) {
          injections = new HashSet<InjectionPoint>();
          injectionsByClass.put(enclosingElement, injections);
        }

        // Assemble information on the injection point.
        TypeMirror elementType = element.asType();
        String variableName = element.getSimpleName().toString();

        final InjectExtra annotation = element.getAnnotation(InjectExtra.class);
        if (elementType.getKind().isPrimitive()) {
          // Get wrapper for primitive types
          switch(elementType.getKind()) {
            case BOOLEAN:
              elementType = booleanType;
              break;
            case BYTE:
              elementType = byteType;
              break;
            case SHORT:
              elementType = shortType;
              break;
            case INT:
              elementType = integerType;
              break;
            case LONG:
              elementType = longType;
              break;
            case CHAR:
              elementType = characterType;
              break;
            case FLOAT:
              elementType = floatType;
              break;
            case DOUBLE:
              elementType = doubleType;
              break;
          }
        }
        injections.add(new InjectionPoint(variableName, annotation.value(), annotation.optional(), elementType));
      }

      for (Map.Entry<TypeElement, Set<InjectionPoint>> injection : injectionsByClass.entrySet()) {
        TypeElement type = injection.getKey();
        Set<InjectionPoint> injectionPoints = injection.getValue();

        String targetType = type.getQualifiedName().toString();
        String packageName = elementUtils.getPackageOf(type).getQualifiedName().toString();
        String className =
            type.getQualifiedName().toString().substring(packageName.length() + 1).replace('.', '$')
                + SUFFIX;

        StringBuilder injections = new StringBuilder();
        for (InjectionPoint injectionPoint : injectionPoints) {
          injections.append(injectionPoint).append("\n");
        }

        // Write the extra injector class.
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

      return false;
    }

    private static class InjectionPoint {
      private final String variableName;
      private final String value;
      private final boolean isOptional;
      private final TypeMirror variableType;

      InjectionPoint(String variableName, String value, boolean isOptional, TypeMirror variableType) {
        this.variableName = variableName;
        this.value = value;
        this.isOptional = isOptional;
        this.variableType = variableType;
      }

      @Override public String toString() {
        if (isOptional) {
          return String.format(INJECTION_OPT, variableName, value, variableType);
        } else {
          return String.format(INJECTION, variableName, value, variableType);
        }
      }
    }

    private static final String INJECTION = "    target.%1$s = (%3$s) finder.getExtra(target, \"%2$s\");";
    private static final String INJECTION_OPT =
        "    if (finder.hasExtra(target, \"%2$s\")) target.%1$s = (%3$s) finder.getExtra(target, \"%2$s\");";
    private static final String INJECTOR = ""
        + "// Generated code from Butter Knife. Do not modify!\n"
        + "package %s;\n\n"
        + "import butterknife.Extras.Finder;\n\n"
        + "public class %s {\n"
        + "  public static void inject(Finder finder, %s target)\n"
        + "        throws butterknife.Extras.MissingExtraException {\n"
        + "%s"
        + "  }\n"
        + "}\n";
  }

  public static class MissingExtraException extends Exception {
    public MissingExtraException(String message) {
      super(message);
    }
  }
}
