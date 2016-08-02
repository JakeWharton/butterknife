package butterknife.butterui;

import android.support.annotation.LayoutRes;

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

    @Ignore
    @Test
    public void testWithAnnotationOnCreate() {
        // TODO: Mock the inflater

        FragmentWithAnnotation fragment = Robolectric.buildFragment(FragmentWithAnnotation.class).create().get();

        assertNotNull("Method was not called", fragment.layoutResID);
        assertEquals(FragmentWithAnnotation.res, fragment.layoutResID.intValue());

        assertNotNull("It must be because ButterKnife.bind() was not called", fragment.getUnbinder());
    }
}