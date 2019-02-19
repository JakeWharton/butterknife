package butterknife;

import androidx.annotation.AttrRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bind a field to the specified attribute ID.
 */

@Target(FIELD)
@Retention(RUNTIME)
public @interface BindAttr {
  @AttrRes int value();
}
