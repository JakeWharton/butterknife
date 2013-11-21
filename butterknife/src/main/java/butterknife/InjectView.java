package butterknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotation for fields which indicate that it should be looked up in the view or activity layout.
 * The view will automatically be cast to the field type.
 * <pre><code>
 * {@literal @}InjectView(R.id.title) TextView title;
 * </code></pre>
 *
 * @see Optional
 */
@Retention(CLASS) @Target(FIELD)
public @interface InjectView {
  int value();
}
