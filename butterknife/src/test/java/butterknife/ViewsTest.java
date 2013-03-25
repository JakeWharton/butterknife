package butterknife;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ButterKnifeTestRunner.class)
public class ViewsTest {
  @Test public void zeroInjectionsDoesNotThrowException() {
    class Example {
    }

    Example example = new Example();
    Views.inject(example, null, null);
  }
}
