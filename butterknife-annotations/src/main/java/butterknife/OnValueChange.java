package butterknife;

import android.support.annotation.IdRes;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;

import static android.widget.NumberPicker.OnValueChangeListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a method to an {@link OnValueChangeListener OnValueChangeListener} on the view for each ID
 * specified.
 * <pre><code>
 * {@literal @}OnValueChangeListener(R.id.example) void onValueChange(int old, int new) {
 *   Toast.makeText(this, String.format("Old val %1$d, New val %2$d", old, new), Toast.LENGTH_SHORT)
 *   .show();
 * }
 * </code></pre>
 * Any number of parameters from {@link OnValueChangeListener#onValueChange(
 * android.widget.NumberPicker, int, int)}
 * may be used on the method.
 *
 * @see OnValueChangeListener
 */
@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
        targetType = "android.widget.NumberPicker",
        setter = "setOnValueChangedListener",
        type = "android.widget.NumberPicker.OnValueChangeListener",
        method = @ListenerMethod(
                name = "onValueChange",
                parameters = {"android.widget.NumberPicker",
                        "int",
                        "int"
                }
        )
)
public @interface OnValueChange {
    /** View IDs to which the method will be bound. */
    @IdRes int[] value() default { View.NO_ID };
}
