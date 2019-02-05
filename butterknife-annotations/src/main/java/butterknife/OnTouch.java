package butterknife;

import android.view.View;
import androidx.annotation.IdRes;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.view.View.OnTouchListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bind a method to an {@link OnTouchListener OnTouchListener} on the view for each ID specified.
 * <pre><code>
 * {@literal @}OnTouch(R.id.example) boolean onTouch() {
 *   Toast.makeText(this, "Touched!", Toast.LENGTH_SHORT).show();
 *   return false;
 * }
 * </code></pre>
 * Any number of parameters from
 * {@link OnTouchListener#onTouch(android.view.View, android.view.MotionEvent) onTouch} may be used
 * on the method.
 * <p>
 * If the return type of the method is {@code void}, true will be returned from the listener.
 *
 * @see OnTouchListener
 */
@Target(METHOD)
@Retention(RUNTIME)
@ListenerClass(
    targetType = "android.view.View",
    setter = "setOnTouchListener",
    type = "android.view.View.OnTouchListener",
    method = @ListenerMethod(
        name = "onTouch",
        parameters = {
            "android.view.View",
            "android.view.MotionEvent"
        },
        returnType = "boolean",
        defaultReturn = "true"
    )
)
public @interface OnTouch {
  /** View IDs to which the method will be bound. */
  @IdRes int[] value() default { View.NO_ID };
}
