package butterknife;

import androidx.annotation.ColorRes;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bind a field to the specified color resource ID. Type can be {@code int} or
 * {@link android.content.res.ColorStateList}.
 * <pre><code>
 * {@literal @}BindColor(R.color.background_green) int green;
 * {@literal @}BindColor(R.color.background_green_selector) ColorStateList greenSelector;
 * </code></pre>
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface BindColor {
  /** Color resource ID to which the field will be bound. */
  @ColorRes int value();
}
