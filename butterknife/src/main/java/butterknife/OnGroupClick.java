package butterknife;

import android.view.View;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.widget.ExpandableListView.OnGroupClickListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a method to an {@link OnGroupClickListener OnGroupClickListener} on the view for each ID
 * specified.
 * <pre><code>
 * {@literal @}OnGroupClick(R.id.example_list) void onGroupClick(int position) {
 *   Toast.makeText(this, "Clicked group position " + position + "!", Toast.LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from {@link OnGroupClickListener#onGroupClick(
 * android.widget.ExpandableListView, android.view.View, int, long) onGroupClick} may be used on
 * the method.
 */
@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
    targetType = "android.widget.ExpandableListView",
    setter = "setOnGroupClickListener",
    type = "android.widget.ExpandableListView.OnGroupClickListener",
    method = @ListenerMethod(
        name = "onGroupClick",
        parameters = {
            "android.widget.ExpandableListView",
            "android.view.View",
            "int",
            "long"
        },
        returnType = "boolean",
        defaultReturn = "false"
    )
)
public @interface OnGroupClick {
  /** View IDs to which the method will be bound. */
  int[] value() default { View.NO_ID };
}
