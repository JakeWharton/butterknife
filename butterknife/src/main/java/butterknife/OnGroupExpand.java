package butterknife;

import android.view.View;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.widget.ExpandableListView.OnGroupExpandListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a method to an {@link OnGroupExpandListener OnGroupExpandListener} on the view for each ID
 * specified.
 * <pre><code>
 * {@literal @}OnGroupExpand(R.id.example_list) void onGroupExpand(int position) {
 *   Toast.makeText(this, "Expanded group position " + position + "!", Toast.LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from {@link OnGroupExpandListener#onGroupExpand(int) onGroupExpand}
 * may be used on the method.
 */
@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
    targetType = "android.widget.ExpandableListView",
    setter = "setOnGroupExpandListener",
    type = "android.widget.ExpandableListView.OnGroupExpandListener",
    method = @ListenerMethod(
        name = "onGroupExpand",
        parameters = {
            "int"
        },
        returnType = "void"
    )
)
public @interface OnGroupExpand {
  /** View IDs to which the method will be bound. */
  int[] value() default { View.NO_ID };
}
