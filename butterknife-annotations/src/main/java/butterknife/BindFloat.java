package butterknife;

import androidx.annotation.DimenRes;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bind a field to the specified dimension resource ID whose type is explicitly defined as float.
 * <p>
 * This is different than simply reading a normal dimension as a float value which
 * {@link BindDimen @BindDimen} supports. The resource must be defined as a float like
 * {@code <item name="whatever" format="float" type="dimen">1.1</item>}.
 * <pre><code>
 * {@literal @}BindFloat(R.dimen.image_ratio) float imageRatio;
 * </code></pre>
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface BindFloat {
  /** Float resource ID to which the field will be bound. */
  @DimenRes int value();
}
