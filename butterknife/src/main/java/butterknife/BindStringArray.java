package butterknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field to the specified string array resource ID.
 * <pre><code>
 * {@literal @}BindStringArray(R.array.countries) String[] countries;
 * </code></pre>
 */
@Retention(CLASS) @Target(FIELD)
public @interface BindStringArray {
  /** String array resource ID to which the field will be bound. */
  int value();
}
