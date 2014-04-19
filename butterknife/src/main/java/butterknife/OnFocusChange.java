package butterknife;

import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.view.View.OnFocusChangeListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotation for methods which indicate that they should be called when a view's focus changed.
 * Corresponds to adding an {@link OnFocusChangeListener OnFocusChangeListener} to the views
 * specified.
 * <pre><code>
 * {@literal @}OnFocusChanged(R.id.example) void onFocusChanged(boolean focused) {
 *   Toast.makeText(this, focused ? "Gained focus" : "Lost focus", LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from {@link OnFocusChangeListener#onFocusChange(android.view.View,
 * boolean) onFocusChange} may be used on the method.
 *
 * @see OnFocusChangeListener
 * @see Optional
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
  int[] value();
}
