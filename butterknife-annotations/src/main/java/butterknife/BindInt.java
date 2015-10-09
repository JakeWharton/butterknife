package butterknife;

import android.support.annotation.IntegerRes;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field to the specified integer resource ID.
 * <pre><code>
 * {@literal @}BindInt(R.int.columns) int columns;
 * </code></pre>
 */
@Retention(CLASS) @Target(FIELD)
public @interface BindInt {
  /** Integer resource ID to which the field will be bound. */
  @IntegerRes int value();
}
