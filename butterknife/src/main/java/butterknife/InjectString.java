package butterknife;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import butterknife.internal.ResourceClass;

/**
 * Bind a field to the String for the specified ID.
 * <pre><code>
 * {@literal @}InjectString(R.string.title) String title;
 * </code> </pre>
 */
@Retention(CLASS) @Target(FIELD)
@ResourceClass(targetType = { "java.lang.String" } , getter = "getString")
public @interface InjectString {
  /** String ID to which the field will be bound. */
  int value();
}