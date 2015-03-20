package butterknife;

import android.view.View;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a method to an {@link OnTabChanged OnTabChanged} on the view for each ID
 * specified.
 * <pre><code>
 * {@literal @}OnTabChanged(R.id.example) void onTabChanged(String tabId) {
 *   Toast.makeText(this, "You selected tabId: " + tabId, LENGTH_SHORT).show();
 * }
 * </code></pre>
 *
 * @see Optional
 */
@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
    targetType = "android.widget.TabHost",
    setter = "setOnTabChangedListener",
    type = "android.widget.TabHost.OnTabChangeListener",
    method = @ListenerMethod(
        name = "onTabChanged",
        parameters = {
            "java.lang.String"
        }
    )
)

public @interface OnTabChanged {
  /** View IDs to which the method will be bound. */
  int[] value() default { View.NO_ID };
}
