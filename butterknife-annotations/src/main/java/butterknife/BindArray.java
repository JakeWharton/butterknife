package butterknife;

import androidx.annotation.ArrayRes;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bind a field to the specified array resource ID. The type of array will be inferred from the
 * annotated element.
 *
 * String array:
 * <pre><code>
 * {@literal @}BindArray(R.array.countries) String[] countries;
 * </code></pre>
 *
 * Int array:
 * <pre><code>
 * {@literal @}BindArray(R.array.phones) int[] phones;
 * </code></pre>
 *
 * Text array:
 * <pre><code>
 * {@literal @}BindArray(R.array.options) CharSequence[] options;
 * </code></pre>
 *
 * {@link android.content.res.TypedArray}:
 * <pre><code>
 * {@literal @}BindArray(R.array.icons) TypedArray icons;
 * </code></pre>
 */
@Retention(RUNTIME) @Target(FIELD)
public @interface BindArray {
  /** Array resource ID to which the field will be bound. */
  @ArrayRes int value();
}
