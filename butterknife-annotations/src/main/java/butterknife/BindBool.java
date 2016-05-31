package butterknife;

import android.support.annotation.BoolRes;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field to the specified boolean resource ID.
 * <pre><code>
 * {@literal @}BindBool(R.bool.is_tablet) boolean isTablet;
 * </code></pre>
 */
@Retention(CLASS) @Target(FIELD)
public @interface BindBool {
  /** Boolean resource ID to which the field will be bound. */
  @BoolRes int value();
}
