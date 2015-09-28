package butterknife;

import android.view.View;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.widget.ExpandableListView.OnGroupCollapseListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a method to an {@link OnGroupCollapseListener OnGroupCollapseListener} on the view for
 * each ID specified.
 * <pre><code>
 * {@literal @}OnGroupCollapse(R.id.example_list) void onGroupCollapse(int position) {
 *   Toast.makeText(this, "Collapseed group position " + position + "!", Toast.LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from
 * {@link OnGroupCollapseListener#onGroupCollapse(int) onGroupCollapse} may be used on the method.
 */
@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
    targetType = "android.widget.ExpandableListView",
    setter = "setOnGroupCollapseListener",
    type = "android.widget.ExpandableListView.OnGroupCollapseListener",
    method = @ListenerMethod(
        name = "onGroupCollapse",
        parameters = {
            "int"
        },
        returnType = "void"
    )
)
public @interface OnGroupCollapse {
  /** View IDs to which the method will be bound. */
  int[] value() default { View.NO_ID };
}
