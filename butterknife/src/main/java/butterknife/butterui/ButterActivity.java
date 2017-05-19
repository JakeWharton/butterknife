package butterknife.butterui;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Extenders of this class can remove another part of the unneeded noise in the class.<br>
 * Before:
 * <pre class="code">
 * public class MainActivity extends {@link Activity} {
 *   &#064;{@link butterknife.BindView}(R.id.title) protected TextView title;
 *
 *   &#064;Override
 *   public void onCreate(Bundle savedInstanceState) {
 *     super.onCreate(savedInstanceState);
 *     setContentView(R.layout.activity_main);
 *     utterKnife.bind(this);
 *   }
 * }
 * </pre>
 * After:
 * <pre class="code">
 * &#064;{@link BindLayout}(R.layout.activity_main)
 * public class MainActivity extends ButterActivity {
 *   &#064;{@link butterknife.BindView}(R.id.title) protected TextView title;
 * }
 * </pre>
 * @author
 *      <br>Jul 31 2016 idosu
 */
public abstract class ButterActivity extends Activity {
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind();
    }

    /*
    TODO(idosu): Check if this ias needed or android calls onCreate(Bundle)
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        bind();
    }
    */

    private void bind() {
        setContentView(BindLayoutUtil.getBindLayout(getClass()));
        unbinder = ButterKnife.bind(this);
    }

    /**
     * Gets butterknife unbinder for this activity
     * @return the unbinder
     */
    protected Unbinder getUnbinder() {
        return unbinder;
    }
}