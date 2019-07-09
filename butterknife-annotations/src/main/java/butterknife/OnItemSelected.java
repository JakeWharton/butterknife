package butterknife;

import android.view.View;
import androidx.annotation.IdRes;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.widget.AdapterView.OnItemSelectedListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a method to an {@link OnItemSelectedListener OnItemSelectedListener} on the view for each
 * ID specified.
 * <pre><code>
 * {@literal @}OnItemSelected(R.id.example_list) void onItemSelected(int position) {
 *   Toast.makeText(this, "Selected position " + position + "!", Toast.LENGTH_SHORT).show();
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
 *   Toast.makeText(this, "Nothing selected!", Toast.LENGTH_SHORT).show();
 * }
 * </code></pre>
 *
 * @see OnItemSelectedListener
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
  /** View IDs to which the method will be bound. */
  @IdRes int[] value() default { View.NO_ID };

  /** Listener callback to which the method will be bound. */
  Callback callback() default Callback.ITEM_SELECTED;

  /** {@link OnItemSelectedListener} callback methods. */
  enum Callback {
    /**
     * {@link OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View,
     * int, long)}
     */
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

    /** {@link OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)} */
    @ListenerMethod(
        name = "onNothingSelected",
        parameters = "android.widget.AdapterView<?>"
    )
    NOTHING_SELECTED
  }
}
