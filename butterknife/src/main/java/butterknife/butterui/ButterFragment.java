package butterknife.butterui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Extenders of this class can remove another part of the unneeded noise in the class.<br>
 * Before:
 * <pre class="code">
 * public class FancyFragment extends {@link Fragment} {
 *   &#064;{@link butterknife.BindView}(R.id.title) protected TextView title;
 *
 *   &#064;Override
 *   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 *     View view = inflater.inflate(R.layout.fancy_fragment, container);
 *     ButterKnife.bind(this);
 *     return view;
 *   }
 * }
 * </pre>
 * After:
 * <pre class="code">
 * &#064;{@link BindLayout}(R.layout.fancy_fragment)
 * public class FancyFragment extends ButterFragment {
 *   &#064;{@link butterknife.BindView}(R.id.title) protected TextView title;
 * }
 * </pre>
 * @author
 *      <br>Jul 31 2016 idosu
 */
public abstract class ButterFragment extends Fragment {
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO(idosu): Add a way to configure attachToRoot
        View view = inflater.inflate(BindLayoutUtil.getBindLayout(getClass()), container);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    /**
     * Gets butterknife unbinder for this fragment
     * @return the unbinder
     */
    protected Unbinder getUnbinder() {
        return unbinder;
    }
}