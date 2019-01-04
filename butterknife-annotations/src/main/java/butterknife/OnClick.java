package butterknife;

import android.view.View;
import androidx.annotation.IdRes;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.view.View.OnClickListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bind a method to an {@link OnClickListener OnClickListener} on the view for each ID specified.
 * <pre><code>
 * {@literal @}OnClick(R.id.example) void onClick() {
 *   Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from
 * {@link OnClickListener#onClick(android.view.View) onClick} may be used on the
 * method.
 *
 * @see OnClickListener
 */
@Target(METHOD)
@Retention(RUNTIME)
@ListenerClass(
    targetType = "android.view.View",
    setter = "setOnClickListener",
    type = "butterknife.internal.DebouncingOnClickListener",
    method = @ListenerMethod(
        name = "doClick",
        parameters = "android.view.View"
    )
)
public @interface OnClick {
  /** View IDs to which the method will be bound. */
  @IdRes int[] value() default { View.NO_ID };
}
