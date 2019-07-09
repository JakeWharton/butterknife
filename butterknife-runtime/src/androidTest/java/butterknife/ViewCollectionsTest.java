package butterknife;

import android.content.Context;
import android.util.Property;
import android.view.View;
import androidx.test.InstrumentationRegistry;
import java.util.List;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;

public class ViewCollectionsTest {
  private static final Property<View, Boolean> PROPERTY_ENABLED =
      new Property<View, Boolean>(Boolean.class, "enabled") {
        @Override public Boolean get(View view) {
          return view.isEnabled();
        }

        @Override public void set(View view, Boolean enabled) {
          view.setEnabled(enabled);
        }
      };
  private static final Setter<View, Boolean> SETTER_ENABLED =
      (view, value, index) -> view.setEnabled(value);
  private static final Action<View> ACTION_DISABLE = (view, index) -> view.setEnabled(false);
  private static final Action<View> ACTION_ZERO_ALPHA = (view, index) -> view.setAlpha(0f);

  private final Context context = InstrumentationRegistry.getContext();

  @Test public void propertyAppliedToView() {
    View view = new View(context);
    assertThat(view.isEnabled()).isTrue();

    ViewCollections.set(view, PROPERTY_ENABLED, false);
    assertThat(view.isEnabled()).isFalse();
  }

  @Test public void propertyAppliedToEveryViewInList() {
    View view1 = new View(context);
    View view2 = new View(context);
    View view3 = new View(context);
    assertThat(view1.isEnabled()).isTrue();
    assertThat(view2.isEnabled()).isTrue();
    assertThat(view3.isEnabled()).isTrue();

    List<View> views = asList(view1, view2, view3);
    ViewCollections.set(views, PROPERTY_ENABLED, false);

    assertThat(view1.isEnabled()).isFalse();
    assertThat(view2.isEnabled()).isFalse();
    assertThat(view3.isEnabled()).isFalse();
  }

  @Test public void propertyAppliedToEveryViewInArray() {
    View view1 = new View(context);
    View view2 = new View(context);
    View view3 = new View(context);
    assertThat(view1.isEnabled()).isTrue();
    assertThat(view2.isEnabled()).isTrue();
    assertThat(view3.isEnabled()).isTrue();

    View[] views = new View[] { view1, view2, view3 };
    ViewCollections.set(views, PROPERTY_ENABLED, false);

    assertThat(view1.isEnabled()).isFalse();
    assertThat(view2.isEnabled()).isFalse();
    assertThat(view3.isEnabled()).isFalse();
  }

  @Test public void actionAppliedToView() {
    View view = new View(context);
    assertThat(view.isEnabled()).isTrue();

    ViewCollections.run(view, ACTION_DISABLE);

    assertThat(view.isEnabled()).isFalse();
  }

  @Test public void actionsAppliedToView() {
    View view = new View(context);
    assertThat(view.isEnabled()).isTrue();
    assertThat(view.getAlpha()).isEqualTo(1f);

    ViewCollections.run(view, ACTION_DISABLE, ACTION_ZERO_ALPHA);
    assertThat(view.isEnabled()).isFalse();
    assertThat(view.getAlpha()).isEqualTo(0f);
  }

  @Test public void actionAppliedToEveryViewInList() {
    View view1 = new View(context);
    View view2 = new View(context);
    View view3 = new View(context);
    assertThat(view1.isEnabled()).isTrue();
    assertThat(view2.isEnabled()).isTrue();
    assertThat(view3.isEnabled()).isTrue();

    List<View> views = asList(view1, view2, view3);
    ViewCollections.run(views, ACTION_DISABLE);

    assertThat(view1.isEnabled()).isFalse();
    assertThat(view2.isEnabled()).isFalse();
    assertThat(view3.isEnabled()).isFalse();
  }

  @Test public void actionAppliedToEveryViewInArray() {
    View view1 = new View(context);
    View view2 = new View(context);
    View view3 = new View(context);
    assertThat(view1.isEnabled()).isTrue();
    assertThat(view2.isEnabled()).isTrue();
    assertThat(view3.isEnabled()).isTrue();

    View[] views = new View[] { view1, view2, view3 };
    ViewCollections.run(views, ACTION_DISABLE);

    assertThat(view1.isEnabled()).isFalse();
    assertThat(view2.isEnabled()).isFalse();
    assertThat(view3.isEnabled()).isFalse();
  }

  @Test public void actionsAppliedToEveryViewInList() {
    View view1 = new View(context);
    View view2 = new View(context);
    View view3 = new View(context);
    assertThat(view1.isEnabled()).isTrue();
    assertThat(view2.isEnabled()).isTrue();
    assertThat(view3.isEnabled()).isTrue();
    assertThat(view1.getAlpha()).isEqualTo(1f);
    assertThat(view2.getAlpha()).isEqualTo(1f);
    assertThat(view3.getAlpha()).isEqualTo(1f);

    List<View> views = asList(view1, view2, view3);
    ViewCollections.run(views, ACTION_DISABLE, ACTION_ZERO_ALPHA);

    assertThat(view1.isEnabled()).isFalse();
    assertThat(view2.isEnabled()).isFalse();
    assertThat(view3.isEnabled()).isFalse();
    assertThat(view1.getAlpha()).isEqualTo(0f);
    assertThat(view2.getAlpha()).isEqualTo(0f);
    assertThat(view3.getAlpha()).isEqualTo(0f);
  }

  @Test public void actionsAppliedToEveryViewInArray() {
    View view1 = new View(context);
    View view2 = new View(context);
    View view3 = new View(context);
    assertThat(view1.isEnabled()).isTrue();
    assertThat(view2.isEnabled()).isTrue();
    assertThat(view3.isEnabled()).isTrue();
    assertThat(view1.getAlpha()).isEqualTo(1f);
    assertThat(view2.getAlpha()).isEqualTo(1f);
    assertThat(view3.getAlpha()).isEqualTo(1f);

    View[] views = new View[] { view1, view2, view3 };
    ViewCollections.run(views, ACTION_DISABLE, ACTION_ZERO_ALPHA);

    assertThat(view1.isEnabled()).isFalse();
    assertThat(view2.isEnabled()).isFalse();
    assertThat(view3.isEnabled()).isFalse();
    assertThat(view1.getAlpha()).isEqualTo(0f);
    assertThat(view2.getAlpha()).isEqualTo(0f);
    assertThat(view3.getAlpha()).isEqualTo(0f);
  }

  @Test public void setterAppliedToView() {
    View view = new View(context);
    assertThat(view.isEnabled()).isTrue();

    ViewCollections.set(view, SETTER_ENABLED, false);

    assertThat(view.isEnabled()).isFalse();
  }

  @Test public void setterAppliedToEveryViewInList() {
    View view1 = new View(context);
    View view2 = new View(context);
    View view3 = new View(context);
    assertThat(view1.isEnabled()).isTrue();
    assertThat(view2.isEnabled()).isTrue();
    assertThat(view3.isEnabled()).isTrue();

    List<View> views = asList(view1, view2, view3);
    ViewCollections.set(views, SETTER_ENABLED, false);

    assertThat(view1.isEnabled()).isFalse();
    assertThat(view2.isEnabled()).isFalse();
    assertThat(view3.isEnabled()).isFalse();
  }

  @Test public void setterAppliedToEveryViewInArray() {
    View view1 = new View(context);
    View view2 = new View(context);
    View view3 = new View(context);
    assertThat(view1.isEnabled()).isTrue();
    assertThat(view2.isEnabled()).isTrue();
    assertThat(view3.isEnabled()).isTrue();

    View[] views = new View[] { view1, view2, view3 };
    ViewCollections.set(views, SETTER_ENABLED, false);

    assertThat(view1.isEnabled()).isFalse();
    assertThat(view2.isEnabled()).isFalse();
    assertThat(view3.isEnabled()).isFalse();
  }
}
