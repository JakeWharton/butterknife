package butterknife;

import butterknife.internal.ListenerClass;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.widget.TextView.OnEditorActionListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotation for methods which indicate that they should be called when an edit action occurs.
 * Corresponds to adding a {@link OnEditorActionListener} to the views specified by
 * {@link #value()}.
 * <pre><code>
 * {@literal @}OnEditorAction(R.id.example) boolean onEditorAction(KeyEvent key) {
 *   Toast.makeText(this, "Pressed: " + key, LENGTH_SHORT).show();
 *   return true;
 * }
 * </code></pre>
 * Any number of parameters from {@link OnEditorActionListener} may be used on the method.
 *
 * @see OnEditorActionListener
 * @see Optional
 */
@Retention(CLASS) @Target(METHOD)
@ListenerClass(
    targetType = "android.widget.TextView",
    setter = "setOnEditorActionListener",
    type = "android.widget.TextView.OnEditorActionListener",
    name = "onEditorAction",
    parameters = {
        "android.widget.TextView",
        "int",
        "android.view.KeyEvent"
    }
)
public @interface OnEditorAction {
  int[] value();
}
