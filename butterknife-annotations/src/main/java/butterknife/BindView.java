package butterknife;

import android.support.annotation.IdRes;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field to the view for the specified ID. The view will automatically be cast to the field
 * type.
 * <pre><code>
 * {@literal @}BindView(R.id.title) TextView title;
 * </code></pre>
 *
 * If used in a library project where the fields are not final, use the other attributes:
 * <pre><code>
 * {@literal @}BindView(idName = "title") TextView title;
 * </code></pre>
 */
@Retention(CLASS) @Target(FIELD)
public @interface BindView {
  /** View ID to which the field will be bound. If used, this should be specified on it's own. */
  @IdRes int value() default -1;

  /**
   * The name of the ID to use. For example, if the generated ID is referenced by
   * {@code R.id.my_view} then this should be set to {@code "my_view"}.
   *
   * @see #idClass() to specify a generated resource class in a different package.
   */
  String idName() default "";

  /**
   * Generated {@code R.id} class which should be used with {@link #idName}. If not specified, it is
   * assumed there is one available in the package this annotation is used in.
   */
  Class<?> idClass() default void.class;
}
