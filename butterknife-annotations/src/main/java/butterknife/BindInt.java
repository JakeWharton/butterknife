package butterknife;

import androidx.annotation.IntegerRes;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bind a field to the specified integer resource ID.
 * <pre><code>
 * {@literal @}BindInt(R.int.columns) int columns;
 * </code></pre>
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface BindInt {
  /** Integer resource ID to which the field will be bound. */
  @IntegerRes int value();
}
