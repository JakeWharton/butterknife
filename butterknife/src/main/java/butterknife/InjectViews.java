package butterknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field to the view for each ID specified in a {@link List} or array. Each view will
 * automatically be cast to the list or array type.
 * <pre><code>
 * {@literal @}InjectViews({R.id.first_name, R.id.middle_name, R.id.last_name})
 * List&lt;TextView&gt; nameViews;
 *
 * {@literal @}InjectViews({R.id.address_line_1, R.id.address_line_2})
 * EditText[] addressViews;
 * </code></pre>
 */
@Retention(CLASS) @Target(FIELD)
public @interface InjectViews {
  /** View IDs to which the field will be bound. */
  int[] value();
}
