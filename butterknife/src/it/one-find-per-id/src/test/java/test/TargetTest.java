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
    assertThat(target.thing1).isNotNull();
    assertThat(target.thing2).isNotNull();
    assertThat(target.thing3).isNotNull();
    assertThat(target.called).isEqualTo(1);
  }
}
