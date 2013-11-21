package butterknife;

import butterknife.internal.ListenerClass;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.view.View.OnFocusChangeListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotation for methods which indicate that they should be called when a view's focus changed.
 * Corresponds to adding a {@link OnFocusChangeListener} to the views specified by {@link #value()}.
 * <pre><code>
 * {@literal @}OnFocusChanged(R.id.example) void onFocusChanged(boolean focused) {
 *   Toast.makeText(this, focused ? "Gained focus" : "Lost focus", LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from {@link OnFocusChangeListener} may be used on the method.
 *
 * @see OnFocusChangeListener
 * @see Optional
 */
@Retention(CLASS) @Target(METHOD)
@ListenerClass(OnFocusChangeListener.class)
public @interface OnFocusChanged {
  int[] value();
}
