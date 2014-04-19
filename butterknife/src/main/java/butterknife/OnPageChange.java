package butterknife;

import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Annotation for methods which indicate that they should be called when a {@code ViewPager} page
 * has changed. Corresponds to adding an {@code OnPageChangeListener} to the
 * views specified.
 * <pre><code>
 * {@literal @}OnPageChange(R.id.example_pager) void onPageSelected(int position) {
 *   Toast.makeText(this, "Selected " + position + "!", LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from {@code onPageSelected} may be used on the method.
 * <p>
 * To bind to methods other than {@code onPageSelected}, specify a different {@code callback}.
 * <pre><code>
 * {@literal @}OnPageChange(value = R.id.example_pager, callback = PAGE_SCROLL_STATE_CHANGED)
 * void onPageStateChanged(int state) {
 *   Toast.makeText(this, "State changed: " + state + "!", LENGTH_SHORT).show();
 * }
 * </code></pre>
 *
 * @see Optional
 */
@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
    targetType = "android.support.v4.view.ViewPager",
    setter = "setOnPageChangeListener",
    type = "android.support.v4.view.ViewPager.OnPageChangeListener",
    callbacks = OnPageChange.Callback.class
)
public @interface OnPageChange {
  int[] value();
  Callback callback() default Callback.PAGE_SELECTED;

  enum Callback {
    @ListenerMethod(
        name = "onPageSelected",
        parameters = "int"
    )
    PAGE_SELECTED,

    @ListenerMethod(
        name = "onPageScrolled",
        parameters = {
            "int",
            "float",
            "int"
        }
    )
    PAGE_SCROLLED,

    @ListenerMethod(
        name = "onPageScrollStateChanged",
        parameters = "int"
    )
    PAGE_SCROLL_STATE_CHANGED,
  }
}
