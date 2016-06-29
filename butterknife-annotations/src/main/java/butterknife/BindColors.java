package butterknife;

import android.support.annotation.IdRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field to the specified list color resource IDs. Type can be {@code java.lang.Integer} or
 * {@link android.content.res.ColorStateList}.
 * <pre><code>
 * {@literal @}BindColors({R.color.background_green, R.color.background_blue}) List&lt;Integer&gt; colors;
 * {@literal @}BindColors({R.color.background_green_selector, R.color.background_blue_selector}) ColorStateList[] greenSelector;
 * </code></pre>
 */
@Retention(CLASS) @Target(FIELD)
public @interface BindColors {
  @IdRes int[] value();
}
