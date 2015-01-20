package butterknife;

import android.app.Activity;
import android.util.Property;
import android.view.View;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.ANDROID.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.entry;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ButterKnifeTest {
  private static final Property<View, Boolean> PROPERTY_ENABLED =
      new Property<View, Boolean>(Boolean.class, "enabled") {
        @Override public Boolean get(View view) {
          return view.isEnabled();
        }

        @Override public void set(View view, Boolean enabled) {
          view.setEnabled(enabled);
        }
      };
  private static final ButterKnife.Setter<View, Boolean> SETTER_ENABLED =
      new ButterKnife.Setter<View, Boolean>() {
        @Override public void set(View view, Boolean value, int index) {
          view.setEnabled(value);
        }
      };
  private static final ButterKnife.Action<View> ACTION_DISABLE = new ButterKnife.Action<View>() {
    @Override public void apply(View view, int index) {
      view.setEnabled(false);
    }
  };

  @Before @After // Clear out cache of injectors and resetters before and after each test.
  public void resetViewsCache() {
    ButterKnife.INJECTORS.clear();
  }

  @Test public void propertyAppliedToEveryView() {
    View view1 = new View(Robolectric.application);
    View view2 = new View(Robolectric.application);
    View view3 = new View(Robolectric.application);
    assertThat(view1).isEnabled();
    assertThat(view2).isEnabled();
    assertThat(view3).isEnabled();

    List<View> views = Arrays.asList(view1, view2, view3);
    ButterKnife.apply(views, PROPERTY_ENABLED, false);

    assertThat(view1).isDisabled();
    assertThat(view2).isDisabled();
    assertThat(view3).isDisabled();
  }

  @Test public void actionAppliedToEveryView() {
    View view1 = new View(Robolectric.application);
    View view2 = new View(Robolectric.application);
    View view3 = new View(Robolectric.application);
    assertThat(view1).isEnabled();
    assertThat(view2).isEnabled();
    assertThat(view3).isEnabled();

    List<View> views = Arrays.asList(view1, view2, view3);
    ButterKnife.apply(views, ACTION_DISABLE);

    assertThat(view1).isDisabled();
    assertThat(view2).isDisabled();
    assertThat(view3).isDisabled();
  }

  @Test public void setterAppliedToEveryView() {
    View view1 = new View(Robolectric.application);
    View view2 = new View(Robolectric.application);
    View view3 = new View(Robolectric.application);
    assertThat(view1).isEnabled();
    assertThat(view2).isEnabled();
    assertThat(view3).isEnabled();

    List<View> views = Arrays.asList(view1, view2, view3);
    ButterKnife.apply(views, SETTER_ENABLED, false);

    assertThat(view1).isDisabled();
    assertThat(view2).isDisabled();
    assertThat(view3).isDisabled();
  }

  @Test public void zeroInjectionsInjectDoesNotThrowException() {
    class Example {
    }

    Example example = new Example();
    ButterKnife.inject(example, null, null);
    assertThat(ButterKnife.INJECTORS).contains(entry(Example.class, ButterKnife.NOP_INJECTOR));
  }

  @Test public void zeroInjectionsResetDoesNotThrowException() {
    class Example {
    }

    Example example = new Example();
    ButterKnife.reset(example);
    assertThat(ButterKnife.INJECTORS).contains(entry(Example.class, ButterKnife.NOP_INJECTOR));
  }

  @Test public void injectingKnownPackagesIsNoOp() {
    ButterKnife.inject(new Activity());
    assertThat(ButterKnife.INJECTORS).isEmpty();
    ButterKnife.inject(new Object(), new Activity());
    assertThat(ButterKnife.INJECTORS).isEmpty();
  }

  @Test public void finderThrowsNiceError() {
    View view = new View(Robolectric.application);
    try {
      ButterKnife.Finder.VIEW.findRequiredView(view, android.R.id.button1, "yo mama");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("Required view 'button1' with ID "
          + android.R.id.button1
          + " for yo mama was not found. If this view is optional add '@Optional' annotation.");
    }
  }
}
