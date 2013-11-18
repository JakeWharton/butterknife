package butterknife;

import android.app.Activity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.entry;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ButterKnifeTest {
  @Before @After // Clear out cache of injectors and resetters before and after each test.
  public void resetViewsCache() {
    ButterKnife.INJECTORS.clear();
    ButterKnife.RESETTERS.clear();
  }

  @Test public void zeroInjectionsInjectDoesNotThrowException() {
    class Example {
    }

    Example example = new Example();
    ButterKnife.inject(example, null, null);
    assertThat(ButterKnife.INJECTORS).contains(entry(Example.class, ButterKnife.NO_OP));
  }

  @Test public void zeroInjectionsResetDoesNotThrowException() {
    class Example {
    }

    Example example = new Example();
    ButterKnife.reset(example);
    assertThat(ButterKnife.RESETTERS).contains(entry(Example.class, ButterKnife.NO_OP));
  }

  @Test public void injectingKnownPackagesIsNoOp() {
    ButterKnife.inject(new Activity());
    assertThat(ButterKnife.INJECTORS).isEmpty();
    ButterKnife.inject(new Object(), new Activity());
    assertThat(ButterKnife.INJECTORS).isEmpty();
    ButterKnife.reset(new Object());
    assertThat(ButterKnife.RESETTERS).isEmpty();
    ButterKnife.reset(new Activity());
    assertThat(ButterKnife.RESETTERS).isEmpty();
  }
}
