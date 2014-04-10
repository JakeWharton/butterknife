package butterknife;

import butterknife.internal.ListenerClass;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.view.View.OnClickListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotation for methods which indicate that they should be called when a view is clicked.
 * Corresponds to adding a {@link OnClickListener} to the views specified by {@link #value()}.
 * <pre><code>
 * {@literal @}OnClick(R.id.example) void onClick() {
 *   Toast.makeText(this, "Clicked!", LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from {@link OnClickListener} may be used on the method.
 *
 * @see OnClickListener
 * @see Optional
 */
@Retention(CLASS) @Target(METHOD)
@ListenerClass(
    targetType = "android.view.View",
    setter = "setOnClickListener",
    type = "android.view.View.OnClickListener",
    name = "onClick",
    parameters = {
        "android.view.View"
    }
)
public @interface OnClick {
  int[] value();
}
