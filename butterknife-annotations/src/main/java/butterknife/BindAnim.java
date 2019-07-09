package butterknife;

import androidx.annotation.AnimRes;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bind a field to the specified animation resource ID.
 * <pre><code>
 * {@literal @}BindAnim(R.anim.fade_in) Animation fadeIn;
 * </code></pre>
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface BindAnim {
  /** Animation resource ID to which the field will be bound. */
  @AnimRes int value();
}
