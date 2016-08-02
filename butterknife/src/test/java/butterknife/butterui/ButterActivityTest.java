package butterknife.butterui;

import android.support.annotation.LayoutRes;
import android.test.mock.MockContentProvider;
import android.test.mock.MockContentResolver;
import android.test.mock.MockContext;

import org.junit.After;
import org.junit.Before;
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
public class ButterActivityTest {
    private static class ActivityNoAnnotation extends ButterActivity {
    }

    @BindLayout(ActivityWithAnnotation.res)
    private static class ActivityWithAnnotation extends ButterActivity {
        @LayoutRes
        public static final int res = 1337;

        public Integer layoutResID = null;

        // This is a mock with argument capture
        @Override
        public void setContentView(int layoutResID) {
            this.layoutResID = layoutResID;
        }
    }

    @Test
    public void testNoAnnotation() {
        try {
            Robolectric.setupActivity(ActivityNoAnnotation.class);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Must set BindLayout to use this feature", e.getMessage());
        }
    }

    @Test
    public void testWithAnnotationOnCreate1() {
        ActivityWithAnnotation activity = Robolectric.setupActivity(ActivityWithAnnotation.class);

        assertNotNull("Method setContentView was not called", activity.layoutResID);
        assertEquals(ActivityWithAnnotation.res, activity.layoutResID.intValue());

        assertNotNull("It must be because ButterKnife.bind() was not called", activity.getUnbinder());
    }

    @Ignore
    @Test
    public void testWithAnnotationOnCreate2() {
        // TODO: Test this with android:persistableMode="persistAcrossReboots"

        ActivityWithAnnotation activity = Robolectric.setupActivity(ActivityWithAnnotation.class);

        assertNotNull("Method setContentView was not called", activity.layoutResID);
        assertEquals(ActivityWithAnnotation.res, activity.layoutResID.intValue());

        assertNotNull("It must be because ButterKnife.bind() was not called", activity.getUnbinder());
    }
}