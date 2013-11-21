package butterknife;

import butterknife.internal.ListenerClass;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.widget.AdapterView.OnItemClickListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotation for methods which indicate that they should be called when an item is clicked.
 * Corresponds to adding a {@link OnItemClickListener} to the views specified by {@link #value()}.
 * <pre><code>
 * {@literal @}OnItemClick(R.id.example_list) void onItemClick(int position) {
 *   Toast.makeText(this, "Clicked position " + position + "!", LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from {@link OnItemClickListener} may be used on the method.
 *
 * @see OnItemClickListener
 * @see Optional
 */
@Retention(CLASS) @Target(METHOD)
@ListenerClass(OnItemClickListener.class)
public @interface OnItemClick {
  int[] value();
}
