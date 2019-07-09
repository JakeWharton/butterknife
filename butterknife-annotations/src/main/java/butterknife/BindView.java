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
 * {@literal @}BindView(R.id.title) TextView title;
 * </code></pre>
 */
@Retention(RUNTIME) @Target(FIELD)
public @interface BindView {
  /** View ID to which the field will be bound. */
  @IdRes int value();
}
