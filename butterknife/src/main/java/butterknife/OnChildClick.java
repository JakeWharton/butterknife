package butterknife;

import android.view.View;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.widget.ExpandableListView.OnChildClickListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a method to an {@link OnChildClickListener OnChildClickListener} on the view for each ID
 * specified.
 * <pre><code>
 * {@literal @}OnChildClick(R.id.example_list) void onChildClick(int position) {
 *   Toast.makeText(this, "Clicked child position " + position + "!", Toast.LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from {@link OnChildClickListener#onChildClick(android.widget.ExpandableListView,
 * android.view.View, int, long) onChildClick} may be used on the method.
 */
@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
    targetType = "android.widget.ExpandableListView",
    setter = "setOnChildClickListener",
    type = "android.widget.ExpandableListView.OnChildClickListener",
    method = @ListenerMethod(
        name = "onChildClick",
        parameters = {
            "android.widget.ExpandableListView",
            "android.view.View",
            "int",
            "int",
            "long"
        },
        returnType = "boolean",
        defaultReturn = "false"
    )
)
public @interface OnChildClick {
  /** View IDs to which the method will be bound. */
  int[] value() default { View.NO_ID };
}
