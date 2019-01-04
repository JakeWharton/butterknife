package butterknife;

import android.view.View;
import androidx.annotation.IdRes;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.widget.CompoundButton.OnCheckedChangeListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bind a method to an {@link OnCheckedChangeListener OnCheckedChangeListener} on the view for
 * each ID specified.
 * <pre><code>
 * {@literal @}OnCheckedChanged(R.id.example) void onChecked(boolean checked) {
 *   Toast.makeText(this, checked ? "Checked!" : "Unchecked!", Toast.LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from
 * {@link OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
 * onCheckedChanged} may be used on the method.
 *
 * @see OnCheckedChangeListener
 */
@Target(METHOD)
@Retention(RUNTIME)
@ListenerClass(
    targetType = "android.widget.CompoundButton",
    setter = "setOnCheckedChangeListener",
    type = "android.widget.CompoundButton.OnCheckedChangeListener",
    method = @ListenerMethod(
        name = "onCheckedChanged",
        parameters = {
            "android.widget.CompoundButton",
            "boolean"
        }
    )
)
public @interface OnCheckedChanged {
  /** View IDs to which the method will be bound. */
  @IdRes int[] value() default { View.NO_ID };
}
