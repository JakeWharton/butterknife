package butterknife.internal;

import org.junit.Assert;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class UtilsTest {

  @Test public void testCastParam() {
    try {
      Utils.castParam("abc", "Foo", 3, "foo()", 4, Integer.class);
      Assert.fail("Failed to cast exception");
    } catch (IllegalStateException ise) {
      assertThat(ise.getMessage()).contains("foo()");
    }
  }
}
