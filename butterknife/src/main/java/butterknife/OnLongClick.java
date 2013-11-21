package butterknife;

import butterknife.internal.ListenerClass;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.view.View.OnLongClickListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotation for methods which indicate that they should be called when a view is long pressed.
 * Corresponds to adding a {@link OnLongClickListener} to the views specified by {@link #value()}.
 * <pre><code>
 * {@literal @}OnLongClick(R.id.example) boolean onLongClick() {
 *   Toast.makeText(this, "Long clicked!", LENGTH_SHORT).show();
 *   return true;
 * }
 * </code></pre>
 * Any number of parameters from {@link OnLongClickListener} may be used on the method.
 *
 * @see OnLongClickListener
 * @see Optional
 */
@Retention(CLASS) @Target(METHOD)
@ListenerClass(OnLongClickListener.class)
public @interface OnLongClick {
  int[] value();
}
