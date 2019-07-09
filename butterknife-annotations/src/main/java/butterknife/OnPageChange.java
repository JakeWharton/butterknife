package butterknife;

import android.view.View;
import androidx.annotation.IdRes;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bind a method to an {@code OnPageChangeListener} on the view for each ID specified.
 * <pre><code>
 * {@literal @}OnPageChange(R.id.example_pager) void onPageSelected(int position) {
 *   Toast.makeText(this, "Selected " + position + "!", Toast.LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from {@code onPageSelected} may be used on the method.
 * <p>
 * To bind to methods other than {@code onPageSelected}, specify a different {@code callback}.
 * <pre><code>
 * {@literal @}OnPageChange(value = R.id.example_pager, callback = PAGE_SCROLL_STATE_CHANGED)
 * void onPageStateChanged(int state) {
 *   Toast.makeText(this, "State changed: " + state + "!", Toast.LENGTH_SHORT).show();
 * }
 * </code></pre>
 */
@Target(METHOD)
@Retention(RUNTIME)
@ListenerClass(
    targetType = "androidx.viewpager.widget.ViewPager",
    setter = "addOnPageChangeListener",
    remover = "removeOnPageChangeListener",
    type = "androidx.viewpager.widget.ViewPager.OnPageChangeListener",
    callbacks = OnPageChange.Callback.class
)
public @interface OnPageChange {
  /** View IDs to which the method will be bound. */
  @IdRes int[] value() default { View.NO_ID };

  /** Listener callback to which the method will be bound. */
  Callback callback() default Callback.PAGE_SELECTED;

  /** {@code ViewPager.OnPageChangeListener} callback methods. */
  enum Callback {
    /** {@code onPageSelected(int)} */
    @ListenerMethod(
        name = "onPageSelected",
        parameters = "int"
    )
    PAGE_SELECTED,

    /** {@code onPageScrolled(int, float, int)} */
    @ListenerMethod(
        name = "onPageScrolled",
        parameters = {
            "int",
            "float",
            "int"
        }
    )
    PAGE_SCROLLED,

    /** {@code onPageScrollStateChanged(int)} */
    @ListenerMethod(
        name = "onPageScrollStateChanged",
        parameters = "int"
    )
    PAGE_SCROLL_STATE_CHANGED,
  }
}
