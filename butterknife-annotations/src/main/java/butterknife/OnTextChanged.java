package butterknife;

import android.text.TextWatcher;
import android.view.View;
import androidx.annotation.IdRes;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bind a method to a {@link TextWatcher TextWatcher} on the view for each ID specified.
 * <pre><code>
 * {@literal @}OnTextChanged(R.id.example) void onTextChanged(CharSequence text) {
 *   Toast.makeText(this, "Text changed: " + text, Toast.LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from {@link TextWatcher#onTextChanged(CharSequence, int, int, int)
 * onTextChanged} may be used on the method.
 * <p>
 * To bind to methods other than {@code onTextChanged}, specify a different {@code callback}.
 * <pre><code>
 * {@literal @}OnTextChanged(value = R.id.example, callback = BEFORE_TEXT_CHANGED)
 * void onBeforeTextChanged(CharSequence text) {
 *   Toast.makeText(this, "Before text changed: " + text, Toast.LENGTH_SHORT).show();
 * }
 * </code></pre>
 *
 * @see TextWatcher
 */
@Target(METHOD)
@Retention(RUNTIME)
@ListenerClass(
    targetType = "android.widget.TextView",
    setter = "addTextChangedListener",
    remover = "removeTextChangedListener",
    type = "android.text.TextWatcher",
    callbacks = OnTextChanged.Callback.class
)
public @interface OnTextChanged {
  /** View IDs to which the method will be bound. */
  @IdRes int[] value() default { View.NO_ID };

  /** Listener callback to which the method will be bound. */
  Callback callback() default Callback.TEXT_CHANGED;

  /** {@link TextWatcher} callback methods. */
  enum Callback {
    /** {@link TextWatcher#onTextChanged(CharSequence, int, int, int)} */
    @ListenerMethod(
        name = "onTextChanged",
        parameters = {
            "java.lang.CharSequence",
            "int",
            "int",
            "int"
        }
    )
    TEXT_CHANGED,

    /** {@link TextWatcher#beforeTextChanged(CharSequence, int, int, int)} */
    @ListenerMethod(
        name = "beforeTextChanged",
        parameters = {
            "java.lang.CharSequence",
            "int",
            "int",
            "int"
        }
    )
    BEFORE_TEXT_CHANGED,

    /** {@link TextWatcher#afterTextChanged(android.text.Editable)} */
    @ListenerMethod(
        name = "afterTextChanged",
        parameters = "android.text.Editable"
    )
    AFTER_TEXT_CHANGED,
  }
}
