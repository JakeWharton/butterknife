package butterknife;

import android.support.annotation.IdRes;
import android.view.View;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.view.View.OnFocusChangeListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a method to an {@link OnFocusChangeListener OnFocusChangeListener} on the view for each ID
 * specified.
 * <pre><code>
 * {@literal @}OnFocusChange(R.id.example) void onFocusChanged(boolean focused) {
 *   Toast.makeText(this, focused ? "Gained focus" : "Lost focus", Toast.LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from {@link OnFocusChangeListener#onFocusChange(android.view.View,
 * boolean) onFocusChange} may be used on the method.
 *
 * @see OnFocusChangeListener
 */
@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
    targetType = "android.view.View",
    setter = "setOnFocusChangeListener",
    type = "android.view.View.OnFocusChangeListener",
    method = @ListenerMethod(
        name = "onFocusChange",
        parameters = {
            "android.view.View",
            "boolean"
        }
    )
)
public @interface OnFocusChange {
  /** View IDs to which the method will be bound. */
  @IdRes int[] value() default { View.NO_ID };
}
