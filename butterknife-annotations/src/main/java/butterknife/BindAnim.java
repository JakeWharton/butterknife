package butterknife;

import android.support.annotation.AnimRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field to the specified animation resource ID.
 * <pre><code>
 * {@literal @}BindAnim(R.anim.fade_in) Animation fadeIn;
 * </code></pre>
 */
@Retention(CLASS) @Target(FIELD)
public @interface BindAnim {
  /** Animation resource ID to which the field will be bound. */
  @AnimRes int value();
}
