package butterknife.internal;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME) @Target(ANNOTATION_TYPE)
public @interface ListenerClass {
  String targetType();

  /** Name of the setter method on the {@link #targetType() target type} for the listener. */
  String setter();

  /** Fully-qualified class name of the listener type. */
  String type();

  /** The number of generic arguments for the type. This used used for casting the view. */
  int genericArguments() default 0;

  /** Name of the listener method for which this annotation applies. */
  String name();

  /**
   * List of method parameters in the form "<type> <name>". If the type is not a primitive it must
   * be fully-qualified.
   */
  String[] parameters() default { };

  /** Primative or fully-qualified return type of the listener method. May also be {@code void}. */
  String returnType() default "void";
}
