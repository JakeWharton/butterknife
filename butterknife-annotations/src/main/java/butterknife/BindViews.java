package butterknife;

import androidx.annotation.IdRes;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bind a field to the view for the specified ID. The view will automatically be cast to the field
 * type.
 * <pre><code>
 * {@literal @}BindViews({ R.id.title, R.id.subtitle })
 * List&lt;TextView&gt; titles;
 * </code></pre>
 */
@Retention(RUNTIME) @Target(FIELD)
public @interface BindViews {
  /** View IDs to which the field will be bound. */
  @IdRes int[] value();
}
