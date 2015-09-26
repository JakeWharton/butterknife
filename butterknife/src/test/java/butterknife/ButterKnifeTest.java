package butterknife;

import android.app.Activity;
import android.util.Property;
import android.view.View;
import java.util.Arrays;
import java.util.List;

import butterknife.shadow.EditModeShadowView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static butterknife.ButterKnife.Finder.arrayOf;
import static butterknife.ButterKnife.Finder.listOf;
import static org.fest.assertions.api.ANDROID.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.entry;
import static org.fest.assertions.api.Assertions.fail;

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

  @Before @After // Clear out cache of binders before and after each test.
  public void resetViewsCache() {
    ButterKnife.BINDERS.clear();
  }

  @Test public void listOfFiltersNull() {
    assertThat(listOf(null, null, null)).isEmpty();
    assertThat(listOf("One", null, null)).containsExactly("One");
    assertThat(listOf(null, "One", null)).containsExactly("One");
    assertThat(listOf(null, null, "One")).containsExactly("One");
    assertThat(listOf("One", "Two", null)).containsExactly("One", "Two");
    assertThat(listOf("One", null, "Two")).containsExactly("One", "Two");
    assertThat(listOf(null, "One", "Two")).containsExactly("One", "Two");
  }

  @Test public void arrayOfFiltersNull() {
    assertThat(arrayOf(null, null, null)).isEmpty();
    assertThat(arrayOf("One", null, null)).containsExactly("One");
    assertThat(arrayOf(null, "One", null)).containsExactly("One");
    assertThat(arrayOf(null, null, "One")).containsExactly("One");
    assertThat(arrayOf("One", "Two", null)).containsExactly("One", "Two");
    assertThat(arrayOf("One", null, "Two")).containsExactly("One", "Two");
    assertThat(arrayOf(null, "One", "Two")).containsExactly("One", "Two");
  }

  @Test public void propertyAppliedToView() {
    View view = new View(Robolectric.application);
    assertThat(view).isEnabled();
    ButterKnife.apply(view, PROPERTY_ENABLED, false);

    assertThat(view).isDisabled();
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

  @Test public void actionAppliedToView() {
    View view = new View(Robolectric.application);
    assertThat(view).isEnabled();

    ButterKnife.apply(view, ACTION_DISABLE);

    assertThat(view).isDisabled();
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

  @Test public void setterAppliedToView() {
    View view = new View(Robolectric.application);
    assertThat(view).isEnabled();

    ButterKnife.apply(view, SETTER_ENABLED, false);

    assertThat(view).isDisabled();
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

  @Test public void bindingViewReturnsView() {
    View view = new View(Robolectric.application);

    View one = ButterKnife.bind(view);
    assertThat(one).isSameAs(view);

    View two = ButterKnife.bind(new Object(), view);
    assertThat(two).isSameAs(view);
  }

  @Test public void zeroBindingsBindDoesNotThrowException() {
    class Example {
    }

    Example example = new Example();
    ButterKnife.bind(example, null, null);
    assertThat(ButterKnife.BINDERS).contains(entry(Example.class, ButterKnife.NOP_VIEW_BINDER));
  }

  @Test public void zeroBindingsUnbindDoesNotThrowException() {
    class Example {
    }

    Example example = new Example();
    ButterKnife.unbind(example);
    assertThat(ButterKnife.BINDERS).contains(entry(Example.class, ButterKnife.NOP_VIEW_BINDER));
  }

  @Test public void bindingKnownPackagesIsNoOp() {
    ButterKnife.bind(new Activity());
    assertThat(ButterKnife.BINDERS).isEmpty();
    ButterKnife.bind(new Object(), new Activity());
    assertThat(ButterKnife.BINDERS).isEmpty();
  }

  @Test public void finderThrowsNiceError() {
    View view = new View(Robolectric.application);
    try {
      ButterKnife.Finder.VIEW.findRequiredView(view, android.R.id.button1, "yo mama");
      fail("View 'button1' with ID " + android.R.id.button1 + " should not have been found.");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("Required view 'button1' with ID "
          + android.R.id.button1
          + " for yo mama was not found. If this view is optional add '@Nullable' annotation.");
    }
  }

  @Config(shadows = EditModeShadowView.class)
  @Test public void finderThrowsLessNiceErrorInEditMode() {
    View view = new View(Robolectric.application);
    try {
      ButterKnife.Finder.VIEW.findRequiredView(view, android.R.id.button1, "yo mama");
      fail("View 'button1' with ID " + android.R.id.button1 + " should not have been found.");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("Required view '<unavailable while editing>' "
          + "with ID " + android.R.id.button1
          + " for yo mama was not found. If this view is optional add '@Nullable' annotation.");
    }
  }
}
