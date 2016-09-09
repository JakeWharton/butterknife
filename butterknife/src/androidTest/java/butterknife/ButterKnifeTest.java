package butterknife;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.util.Property;
import android.view.View;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;

@TargetApi(ICE_CREAM_SANDWICH)
@SdkSuppress(minSdkVersion = ICE_CREAM_SANDWICH)
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
        @Override public void set(@NonNull View view, Boolean value, int index) {
          view.setEnabled(value);
        }
      };
  private static final ButterKnife.Action<View> ACTION_DISABLE = new ButterKnife.Action<View>() {
    @Override public void apply(@NonNull View view, int index) {
      view.setEnabled(false);
    }
  };
  private static final ButterKnife.Action<View> ACTION_ZERO_ALPHA = new ButterKnife.Action<View>() {
    @Override public void apply(@NonNull View view, int index) {
      view.setAlpha(0f);
    }
  };

  private final Context context = InstrumentationRegistry.getContext();

  @Before @After // Clear out cache of binders before and after each test.
  public void resetViewsCache() {
    ButterKnife.BINDINGS.clear();
  }

  @Test public void propertyAppliedToView() {
    View view = new View(context);
    assertThat(view.isEnabled()).isTrue();

    ButterKnife.apply(view, PROPERTY_ENABLED, false);
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
    ButterKnife.apply(views, PROPERTY_ENABLED, false);

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
    ButterKnife.apply(views, PROPERTY_ENABLED, false);

    assertThat(view1.isEnabled()).isFalse();
    assertThat(view2.isEnabled()).isFalse();
    assertThat(view3.isEnabled()).isFalse();
  }

  @Test public void actionAppliedToView() {
    View view = new View(context);
    assertThat(view.isEnabled()).isTrue();

    ButterKnife.apply(view, ACTION_DISABLE);

    assertThat(view.isEnabled()).isFalse();
  }

  @Test public void actionsAppliedToView() {
    View view = new View(context);
    assertThat(view.isEnabled()).isTrue();
    assertThat(view.getAlpha()).isEqualTo(1f);

    ButterKnife.apply(view, ACTION_DISABLE, ACTION_ZERO_ALPHA);
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
    ButterKnife.apply(views, ACTION_DISABLE);

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
    ButterKnife.apply(views, ACTION_DISABLE);

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
    ButterKnife.apply(views, ACTION_DISABLE, ACTION_ZERO_ALPHA);

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
    ButterKnife.apply(views, ACTION_DISABLE, ACTION_ZERO_ALPHA);

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

    ButterKnife.apply(view, SETTER_ENABLED, false);

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
    ButterKnife.apply(views, SETTER_ENABLED, false);

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
    ButterKnife.apply(views, SETTER_ENABLED, false);

    assertThat(view1.isEnabled()).isFalse();
    assertThat(view2.isEnabled()).isFalse();
    assertThat(view3.isEnabled()).isFalse();
  }

  @Test public void zeroBindingsBindDoesNotThrowException() {
    class Example {
    }

    Example example = new Example();
    assertThat(ButterKnife.bind(example, (View) null)).isSameAs(Unbinder.EMPTY);
  }

  @Test public void bindingKnownPackagesIsNoOp() {
    View view = new View(context);
    ButterKnife.bind(view);
    assertThat(ButterKnife.BINDINGS).isEmpty();
    ButterKnife.bind(new Object(), view);
    assertThat(ButterKnife.BINDINGS).isEmpty();
  }
}
