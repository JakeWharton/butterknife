package butterknife;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MethodExampleTest {
  @Test public void missingOptionalViewIsValid() {
    MethodExample.HAS_ONE = true;
    MethodExample example = Robolectric.buildActivity(MethodExample.class).get();
    Views.inject(example);
  }

  @Test public void missingRequiredViewThrowsException() {
    MethodExample.HAS_ONE = false;
    MethodExample example = Robolectric.buildActivity(MethodExample.class).get();
    try {
      Views.inject(example);
    } catch (Views.UnableToInjectException e) {
      assertThat(e.getCause().getCause()).hasMessage(
          "Required view with id '1' for method 'doStuff' was not found. If this method binding is optional add '@Optional'.");
    }
  }
}
