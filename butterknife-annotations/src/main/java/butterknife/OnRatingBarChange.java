package butterknife;

import android.support.annotation.IdRes;
import android.view.View;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a method to an {@code OnRatingBarChangeListener} on the view for each ID specified.
 * <pre><code>
 * {@literal @}OnRatingBarChange(R.id.example) void onRatingBarChange(float rating) {
 *   Toast.makeText(this, "Rating changed: " + rating, LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from {@code onRatingChanged} may be used on the method.
 */
@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
    targetType = "android.widget.RatingBar",
    setter = "setOnRatingBarChangeListener",
    type = "android.widget.RatingBar.OnRatingBarChangeListener",
    method = @ListenerMethod(
        name = "onRatingChanged",
        parameters = {
            "android.widget.RatingBar",
            "float",
            "boolean"
        }
    )
)
public @interface OnRatingBarChange {
  /** View IDs to which the method will be bound. */
  @IdRes int[] value() default { View.NO_ID };
}
