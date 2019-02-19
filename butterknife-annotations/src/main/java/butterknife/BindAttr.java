package butterknife;

import androidx.annotation.AttrRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bind a field to the specified attribute ID.
 * Now it supports only colors presented as {@code int} (annotated with {@link androidx.annotation.ColorInt})
 * and {@link android.content.res.ColorStateList}:
 * <pre><code>
 * {@literal @}BindColor(R.attr.colorAccent)
 * {@literal @}ColorInt
 * int colorAccent;
 * {@literal @}BindColor(R.attr.someTextColor)
 * ColorStateList textColor;
 * </code></pre>
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface BindAttr {
  @AttrRes int value();
}
