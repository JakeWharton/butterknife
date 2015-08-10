package butterknife.internal;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME) @Target(FIELD)
public @interface ListenerMethod {
  /** Name of the listener method for which this annotation applies. */
  String name();

  /** List of method parameters. If the type is not a primitive it must be fully-qualified. */
  String[] parameters() default { };

  /** Primitive or fully-qualified return type of the listener method. May also be {@code void}. */
  String returnType() default "void";

  /** If {@link #returnType()} is not {@code void} this value is returned when no binding exists. */
  String defaultReturn() default "null";
}
