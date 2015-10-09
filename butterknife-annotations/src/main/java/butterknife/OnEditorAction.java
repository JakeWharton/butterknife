package butterknife;

import android.support.annotation.IdRes;
import android.view.View;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.widget.TextView.OnEditorActionListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a method to an {@link OnEditorActionListener OnEditorActionListener} on the view for each
 * ID specified.
 * <pre><code>
 * {@literal @}OnEditorAction(R.id.example) boolean onEditorAction(KeyEvent key) {
 *   Toast.makeText(this, "Pressed: " + key, Toast.LENGTH_SHORT).show();
 *   return true;
 * }
 * </code></pre>
 * Any number of parameters from
 * {@link OnEditorActionListener#onEditorAction(android.widget.TextView, int, android.view.KeyEvent)
 * onEditorAction} may be used on the method.
 *
 * @see OnEditorActionListener
 */
@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
    targetType = "android.widget.TextView",
    setter = "setOnEditorActionListener",
    type = "android.widget.TextView.OnEditorActionListener",
    method = @ListenerMethod(
        name = "onEditorAction",
        parameters = {
            "android.widget.TextView",
            "int",
            "android.view.KeyEvent"
        },
        returnType = "boolean",
        defaultReturn = "false"
    )
)
public @interface OnEditorAction {
  /** View IDs to which the method will be bound. */
  @IdRes int[] value() default { View.NO_ID };
}
