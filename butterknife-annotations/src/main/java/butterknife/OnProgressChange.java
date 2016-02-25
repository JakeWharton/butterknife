package butterknife;

import android.support.annotation.IdRes;
import android.view.View;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bind a method to an {@code OnSeekBarChangeListener} on the view for each ID specified.
 * <pre><code>
 * {@literal @}OnProgressChange(R.id.example_seekbar) void onProgressChanged(SeekBar seekBar,
 * int progress, boolean fromUser) {
 *   Toast.makeText(this, "progress " + progress + "!", Toast.LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from {@code onProgressChanged} may be used on the method.
 * <p>
 * To bind to methods other than {@code onProgressChanged}, specify a different {@code callback}.
 * <pre><code>
 * {@literal @}OnProgressChange(value = R.id.example_seekbar, callback = START_TRACKING_TOUCH)
 * void onStartTrackingTouch(SeekBar seekBar) {
 *   Toast.makeText(this, "start tracking", Toast.LENGTH_SHORT).show();
 * }
 * </code></pre>
 * <pre><code>
 * {@literal @}OnProgressChange(value = R.id.example_seekbar, callback = STOP_TRACKING_TOUCH)
 * void onStopTrackingTouch(SeekBar seekBar) {
 *   Toast.makeText(this, "stop tracking", Toast.LENGTH_SHORT).show();
 * }
 * </code></pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@ListenerClass(
    targetType = "android.widget.SeekBar",
    setter = "setOnSeekBarChangeListener",
    type = "android.widget.SeekBar.OnSeekBarChangeListener",
    callbacks = OnProgressChange.Callback.class
)
public @interface OnProgressChange {
  /** View IDs to which the method will be bound. */
  @IdRes int[] value() default {View.NO_ID};

  /** Listener callback to which the method will be bound. */
  Callback callback() default Callback.PROGRESS_CHANGED;

  /** {@code ViewPager.OnPageChangeListener} callback methods. */
  enum Callback {
    /** {@code onProgressChanged(android.widget.SeekBar, int, boolean)} */
    @ListenerMethod(
        name = "onProgressChanged",
        parameters = {
            "android.widget.SeekBar",
            "int",
            "boolean"
        }
    )
    PROGRESS_CHANGED,

    /** {@code onStartTrackingTouch(android.widget.SeekBar)} */
    @ListenerMethod(
        name = "onStartTrackingTouch",
        parameters = "android.widget.SeekBar"
    )
    START_TRACKING_TOUCH,

    /** {@code onStopTrackingTouch(android.widget.SeekBar)} */
    @ListenerMethod(
        name = "onStopTrackingTouch",
        parameters = "android.widget.SeekBar"
    )
    STOP_TRACKING_TOUCH,
  }
}
