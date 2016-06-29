package butterknife;

import android.support.annotation.IdRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field to the specified list strings resource IDs. Type can be {@code java.lang.String}.
 * <pre><code>
 * {@literal @}BindStrings({R.string.ok, R.string.cancel}) List&lt;String&gt; strings;
 * {@literal @}BindStrings({R.string.ok, R.string.cancel}) String[] strings;
 * </code></pre>
 */

@Retention(CLASS) @Target(FIELD)
public @interface BindStrings {
  @IdRes int[] value();
}
