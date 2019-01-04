package butterknife;

import androidx.annotation.DimenRes;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bind a field to the specified dimension resource ID. Type can be {@code int} for pixel size or
 * {@code float} for exact amount.
 * <pre><code>
 * {@literal @}BindDimen(R.dimen.horizontal_gap) int gapPx;
 * {@literal @}BindDimen(R.dimen.horizontal_gap) float gap;
 * </code></pre>
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface BindDimen {
  /** Dimension resource ID to which the field will be bound. */
  @DimenRes int value();
}
