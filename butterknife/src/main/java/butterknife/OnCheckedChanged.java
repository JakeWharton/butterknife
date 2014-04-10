package butterknife;

import butterknife.internal.ListenerClass;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.widget.CompoundButton.OnCheckedChangeListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotation for methods which indicate that they should be called when a view is checked or
 * unchecked. Corresponds to adding a {@link OnCheckedChangeListener} to the views specified by
 * {@link #value()}.
 * <pre><code>
 * {@literal @}OnCheckedChanged(R.id.example) void onChecked(boolean checked) {
 *   Toast.makeText(this, checked ? "Checked!" : "Unchecked!", LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from {@link OnCheckedChangeListener} may be used on the method.
 *
 * @see OnCheckedChangeListener
 * @see Optional
 */
@Retention(CLASS) @Target(METHOD)
@ListenerClass(
    targetType = "android.widget.CompoundButton",
    setter = "setOnCheckedChangeListener",
    type = "android.widget.CompoundButton.OnCheckedChangeListener",
    name = "onCheckedChanged",
    parameters = {
        "android.widget.CompoundButton",
        "boolean"
    }
)
public @interface OnCheckedChanged {
  int[] value();
}
