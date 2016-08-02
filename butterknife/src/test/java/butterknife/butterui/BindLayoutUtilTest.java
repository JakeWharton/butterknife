package butterknife.butterui;

import android.support.annotation.LayoutRes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author
 *      <br>Aug 02 2016 idosu
 */
public class BindLayoutUtilTest {
    private static class ClassWithoutAnnotation {
    }

    @BindLayout(ClassWithAnnotation.res)
    private static class ClassWithAnnotation {
        @LayoutRes
        public static final int res = 1337;
    }

    @Test
    public void testGetAnnotationNoAnnotation() {
        String expectedErrorMessage = "Error message " + Math.random();

        try {
            BindLayoutUtil.getAnnotation(ClassWithoutAnnotation.class, BindLayout.class, expectedErrorMessage);
            fail();
        } catch (IllegalStateException e) {
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    @Test
    public void testGetAnnotationWithAnnotation() {
        BindLayout annot = BindLayoutUtil.getAnnotation(ClassWithAnnotation.class, BindLayout.class, "");

        assertEquals(ClassWithAnnotation.res, annot.value());
    }

    @Test
    public void testGetBindLayoutNoAnnotation() {
        try {
            BindLayoutUtil.getBindLayout(ClassWithoutAnnotation.class);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Must set BindLayout to use this feature", e.getMessage());
        }
    }

    @Test
    public void testGetBindLayoutWithAnnotation() {
        int res = BindLayoutUtil.getBindLayout(ClassWithAnnotation.class);

        assertEquals(ClassWithAnnotation.res, res);
    }
}