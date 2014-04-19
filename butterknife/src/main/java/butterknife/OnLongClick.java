package butterknife;

import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.view.View.OnLongClickListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotation for methods which indicate that they should be called when a view is long clicked.
 * Corresponds to adding an {@link OnLongClickListener OnLongClickListener} to the views specified.
 * <pre><code>
 * {@literal @}OnLongClick(R.id.example) boolean onLongClick() {
 *   Toast.makeText(this, "Long clicked!", LENGTH_SHORT).show();
 *   return true;
 * }
 * </code></pre>
 * Any number of parameters from {@link OnLongClickListener#onLongClick(android.view.View)} may be
 * used on the method.
 *
 * @see OnLongClickListener
 * @see Optional
 */
@Retention(CLASS) @Target(METHOD)
@ListenerClass(
    targetType = "android.view.View",
    setter = "setOnLongClickListener",
    type = "android.view.View.OnLongClickListener",
    method = @ListenerMethod(
        name = "onLongClick",
        parameters = {
            "android.view.View"
        },
        returnType = "boolean",
        defaultReturn = "false"
    )
)
public @interface OnLongClick {
  int[] value();
}
