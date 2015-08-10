package butterknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field to the specified drawable resource ID.
 * <pre><code>
 * {@literal @}BindDrawable(R.drawable.placeholder) Drawable placeholder;
 * </code></pre>
 */
@Retention(CLASS) @Target(FIELD)
public @interface BindDrawable {
  /** Drawable resource ID to which the field will be bound. */
  int value();
}
