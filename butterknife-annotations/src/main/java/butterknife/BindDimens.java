package butterknife;

import android.support.annotation.IdRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field to the specified list dimension resource IDs. Type can be {@code java.lang.Integer} or
 * {@link java.lang.Float}.
 * <pre><code>
 * {@literal @}BindDimens({R.dimen.height, R.dimen.width}) List&lt;Integer&gt; dimens;
 * {@literal @}BindDimens({R.dimen.height, R.dimen.width}) Float[] dimens;
 * </code></pre>
 */
@Retention(CLASS) @Target(FIELD)
public @interface BindDimens {
  @IdRes int[] value();
}

