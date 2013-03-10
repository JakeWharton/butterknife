package butterknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User: Nicolas PICON
 * Date: 09/03/13 - 01:22
 */
@Retention(RUNTIME) @Target(FIELD)
public @interface InjectExtra {
  String value();
  boolean optional() default false;
}
