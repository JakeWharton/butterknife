package butterknife;

import butterknife.internal.ListenerClass;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotation for methods which indicate that they should be called when a text view's input
 * changed.
 * Corresponds to adding a {@link android.text.TextWatcher} to the views specified by
 * {@link #value()}.
 * <pre><code>
 * {@literal @}OnTextChanged(R.id.example) void onTextChanged(Editable text) {
 *   Toast.makeText(this, "Text changed: " + text, LENGTH_SHORT).show();
 * }
 * </code></pre>
 *
 * @see android.text.TextWatcher
 * @see Optional
 */
@Retention(CLASS) @Target(METHOD)
@ListenerClass(
    name = "butterknife.internal.SimpleTextWatcher",
    setter = "addTextChangedListener",
    method = "afterTextChanged",
    owner = "android.widget.TextView"
)
public @interface OnTextChanged {
  int[] value();
}
