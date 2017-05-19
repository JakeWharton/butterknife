package butterknife.butterui;

import android.support.annotation.LayoutRes;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Configures:
 * 1. the content view of the activity, see {@link android.app.Activity#setContentView(int)}.
 * 2. the resource to inflate with for a fragment, see {@link android.view.LayoutInflater#inflate(int, ViewGroup)}
 * uage:
 * <pre class="code">
 * &#064;BindLayout(R.layout.activity_main)
 * public class MainActivity extends ButterActivity {
 *   // No need for onCreate, the setContentView and ButterKnife.bind happens automatically
 * }
 * &#064;BindLayout(R.layout.activity_main)
 * public class MyFragment extends ButterFragment {
 *   // No need for onCreateView, the LayoutInflater.inflate and ButterKnife.bind happens automatically
 * }
 * </pre>
 */
// TODO(idosu): Maybe generate a binder for setting the content view, for now plain old reflection will have to do(it only gets the layout value)
@Retention(RUNTIME)
@Target(TYPE)
public @interface BindLayout {
  /** Layout ID. */
  @LayoutRes int value();
}
