package butterknife;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class FieldExampleTest {
  @Test public void missingOptionalViewIsValid() {
    FieldExample.HAS_ONE = true;

    FieldExample example = Robolectric.buildActivity(FieldExample.class).get();

    Views.inject(example);
    assertThat(example.thing1).isNotNull();
    assertThat(example.thing2).isNull();
  }

  @Test public void missingRequiredViewThrowsException() {
    FieldExample.HAS_ONE = false;

    FieldExample example = Robolectric.buildActivity(FieldExample.class).get();

    try {
      Views.inject(example);
    } catch (Views.UnableToInjectException e) {
      assertThat(e.getCause().getCause()).hasMessage(
          "Required view with id '1' for field 'thing1' was not found. If this field binding is optional add '@Optional'.");
    }
  }
}
