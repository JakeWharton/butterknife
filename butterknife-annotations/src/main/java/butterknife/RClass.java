package butterknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Denote the R class to lookup symbols for generated code.
 */
@Retention(SOURCE) @Target(TYPE)
public @interface RClass {

  // The R class
  Class<?> value();
}
