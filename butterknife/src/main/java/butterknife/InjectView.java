package butterknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field to the view for the specified ID. The view will automatically be cast to the field
 * type.
 * <pre><code>
 * {@literal @}InjectView(R.id.title) TextView title;
 * </code></pre>
 *
 * @see Optional
 */
@Retention(CLASS) @Target(FIELD)
public @interface InjectView {
  /** View ID to which the field will be bound. */
  int value();
}
