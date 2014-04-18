package butterknife;

import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.widget.AdapterView.OnItemLongClickListener;
import static android.widget.AdapterView.OnItemSelectedListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotation for methods which indicate that they should be called when an item is selected.
 * Corresponds to adding an {@link OnItemSelectedListener OnItemSelectedListener} to the views
 * specified.
 * <pre><code>
 * {@literal @}OnItemSelected(R.id.example_list) void onItemSelected(int position) {
 *   Toast.makeText(this, "Selected position " + position + "!", LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from
 * {@link OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int,
 * long) onItemSelected} may be used on the method.
 * <p>
 * To bind to methods other than {@code onItemSelected}, specify a different {@code callback}.
 * <pre><code>
 * {@literal @}OnItemSelected(value = R.id.example_list, callback = NOTHING_SELECTED)
 * void onNothingSelected() {
 *   Toast.makeText(this, "Nothing selected!", LENGTH_SHORT).show();
 * }
 * </code></pre>
 *
 * @see OnItemLongClickListener
 * @see Optional
 */
@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
    targetType = "android.widget.AdapterView<?>",
    setter = "setOnItemSelectedListener",
    type = "android.widget.AdapterView.OnItemSelectedListener",
    callbacks = OnItemSelected.Callback.class
)
public @interface OnItemSelected {
  int[] value();
  Callback callback() default Callback.ITEM_SELECTED;

  enum Callback {
    @ListenerMethod(
        name = "onItemSelected",
        parameters = {
            "android.widget.AdapterView<?>",
            "android.view.View",
            "int",
            "long"
        }
    )
    ITEM_SELECTED,

    @ListenerMethod(
        name = "onNothingSelected",
        parameters = "android.widget.AdapterView<?>"
    )
    NOTHING_SELECTED
  }
}
