package butterknife.butterui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author
 *      <br>Aug 02 2016 idosu
 */
@RunWith(RobolectricTestRunner.class)
public class ButterFragmentTest {
    public static class FragmentNoAnnotation extends ButterFragment {
    }

    @BindLayout(FragmentWithAnnotation.res)
    public static class FragmentWithAnnotation extends ButterFragment {
        @LayoutRes
        public static final int res = 1337;

        public Integer layoutResID = null;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return super.onCreateView(
                // This is a mock with argument capture
                new LayoutInflater(null) {
                    @Override public LayoutInflater cloneInContext(Context context) { return null; }

                    @Override
                    public View inflate(int resource, ViewGroup root) {
                        layoutResID = resource;
                        return null;
                    }
                },
                container,
                savedInstanceState
            );
        }
    }

    @Test
    public void testNoAnnotation() {
        try {
            Robolectric.buildFragment(FragmentNoAnnotation.class).create().get();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Must set BindLayout to use this feature", e.getMessage());
        }
    }

    @Test
    public void testWithAnnotationOnCreate() {
        FragmentWithAnnotation fragment = Robolectric.buildFragment(FragmentWithAnnotation.class).create().get();

        assertNotNull("Method was not called", fragment.layoutResID);
        assertEquals(FragmentWithAnnotation.res, fragment.layoutResID.intValue());

        assertNotNull("It must be because ButterKnife.bind() was not called", fragment.getUnbinder());
    }
}