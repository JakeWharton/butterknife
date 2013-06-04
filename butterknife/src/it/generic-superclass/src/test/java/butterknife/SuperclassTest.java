package butterknife;

import bar.TestOne;
import bar.TestTwo;
import foo.BaseThing;
import java.lang.reflect.Method;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.data.MapEntry.entry;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SuperclassTest {
  @Before public void setUp() {
    Views.INJECTORS.clear();
  }

  @Test public void superclassInjection() {
    TestOne target = Robolectric.buildActivity(TestOne.class).get();
    Views.inject(target);

    assertThat(target.thing).isNotNull();
    assertThat(target.baseThing).isNotNull();

    assertThat(Views.INJECTORS).containsKey(TestOne.class);
    assertThat(Views.INJECTORS).doesNotContainKey(BaseThing.class);
  }

  @Test public void onlyParentClassInjection() {
    TestTwo target = Robolectric.buildActivity(TestTwo.class).get();
    Views.inject(target);

    assertThat(target.baseThing).isNotNull();

    assertThat(Views.INJECTORS).containsKey(BaseThing.class);

    Method baseThing = Views.INJECTORS.get(BaseThing.class);
    assertThat(Views.INJECTORS).contains(entry(TestTwo.class, baseThing));
  }
}
