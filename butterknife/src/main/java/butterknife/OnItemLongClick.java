package butterknife;

import butterknife.internal.ListenerClass;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.widget.AdapterView.OnItemLongClickListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotation for methods which indicate that they should be called when an item is long pressed.
 * Corresponds to adding a {@link OnItemLongClickListener} to the views specified by
 * {@link #value()}.
 * <pre><code>
 * {@literal @}OnItemLongClick(R.id.example_list) boolean onItemLongClick(int position) {
 *   Toast.makeText(this, "Long clicked position " + position + "!", LENGTH_SHORT).show();
 *   return true;
 * }
 * </code></pre>
 * Any number of parameters from {@link OnItemLongClickListener} may be used on the method.
 *
 * @see OnItemLongClickListener
 * @see Optional
 */
@Retention(CLASS) @Target(METHOD)
@ListenerClass("android.widget.AdapterView.OnItemLongClickListener")
public @interface OnItemLongClick {
  int[] value();
}
