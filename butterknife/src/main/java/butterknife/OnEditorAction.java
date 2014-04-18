package butterknife;

import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.widget.TextView.OnEditorActionListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotation for methods which indicate that they should be called when an edit action occurs.
 * Corresponds to adding an {@link OnEditorActionListener OnEditorActionListener} to the views
 * specified.
 * <pre><code>
 * {@literal @}OnEditorAction(R.id.example) boolean onEditorAction(KeyEvent key) {
 *   Toast.makeText(this, "Pressed: " + key, LENGTH_SHORT).show();
 *   return true;
 * }
 * </code></pre>
 * Any number of parameters from
 * {@link OnEditorActionListener#onEditorAction(android.widget.TextView, int, android.view.KeyEvent)
 * onEditorAction} may be used on the method.
 *
 * @see OnEditorActionListener
 * @see Optional
 */
@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
    targetType = "android.widget.TextView",
    setter = "setOnEditorActionListener",
    type = "android.widget.TextView.OnEditorActionListener",
    callbacks = OnEditorAction.Callback.class
)
public @interface OnEditorAction {
  int[] value();
  Callback callback() default Callback.EDITOR_ACTION;

  enum Callback {
    @ListenerMethod(
        name = "onEditorAction",
        parameters = {
            "android.widget.TextView",
            "int",
            "android.view.KeyEvent"
        },
        returnType = "boolean",
        defaultReturn = "false"
    )
    EDITOR_ACTION
  }
}
