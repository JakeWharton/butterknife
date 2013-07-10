package test;

import butterknife.Views;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TargetTest {
  @Test public void doIt() {
    Target target = new Target();
    Views.inject(target);

    target.viewOne.performClick();
    assertThat(target.count).isEqualTo(1);

    target.viewTwo.performClick();
    assertThat(target.count).isEqualTo(2);
  }
}
