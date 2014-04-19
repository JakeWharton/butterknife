package butterknife;

import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.view.View.OnClickListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotation for methods which indicate that they should be called when a view is clicked.
 * Corresponds to adding an {@link OnClickListener OnClickListener} to the views specified.
 * <pre><code>
 * {@literal @}OnClick(R.id.example) void onClick() {
 *   Toast.makeText(this, "Clicked!", LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from
 * {@link OnClickListener#onClick(android.view.View) onClick} may be used on the
 * method.
 *
 * @see OnClickListener
 * @see Optional
 */
@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
    targetType = "android.view.View",
    setter = "setOnClickListener",
    type = "android.view.View.OnClickListener",
    method = @ListenerMethod(
        name = "onClick",
        parameters = "android.view.View"
    )
)
public @interface OnClick {
  int[] value();
}
