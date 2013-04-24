package butterknife;

import android.app.Activity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.entry;

@RunWith(ButterKnifeTestRunner.class)
public class ViewsTest {
  @Before public void setUp() {
    Views.INJECTORS.clear();
  }

  @Test public void zeroInjectionsDoesNotThrowException() {
    class Example {
    }

    Example example = new Example();
    Views.inject(example, null, null);
    assertThat(Views.INJECTORS).contains(entry(Example.class, Views.NO_OP));
  }

  @Test public void injectingKnownPackagesIsNoOp() {
    Views.inject(new Activity());
    assertThat(Views.INJECTORS).isEmpty();
    Views.inject(new Object(), new Activity());
    assertThat(Views.INJECTORS).isEmpty();
  }
}
