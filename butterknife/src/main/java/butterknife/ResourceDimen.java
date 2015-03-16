package butterknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field to the specified dimension resource ID. Type can be {@code int} for pixel size or
 * {@code float} for exact amount.
 * <pre><code>
 * {@literal @}ResourceDimen(R.dimen.horizontal_gap) int gapPx;
 * {@literal @}ResourceDimen(R.dimen.horizontal_gap) float gap;
 * </code></pre>
 */
@Retention(CLASS) @Target(FIELD)
public @interface ResourceDimen {
  /** Dimension resource ID to which the field will be bound. */
  int value();
}
