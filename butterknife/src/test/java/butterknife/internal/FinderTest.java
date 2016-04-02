package butterknife.internal;

import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import butterknife.shadow.EditModeShadowView;
import butterknife.BuildConfig;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

@RunWith(RobolectricGradleTestRunner.class)
@Config(
    sdk = 18,
    manifest = Config.NONE,
    constants = BuildConfig.class
)
public final class FinderTest {
  @Test public void finderThrowsNiceError() {
    View view = new View(RuntimeEnvironment.application);
    try {
      Finder.VIEW.findRequiredView(view, android.R.id.button1, "yo mama");
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("Required view 'button1' with ID "
          + android.R.id.button1
          + " for yo mama was not found. If this view is optional add '@Nullable' (fields) or '@Optional' (methods) annotation.");
    }
  }

  @Config(shadows = EditModeShadowView.class)
  @Test public void finderThrowsLessNiceErrorInEditMode() {
    View view = new View(RuntimeEnvironment.application);
    try {
      Finder.VIEW.findRequiredView(view, android.R.id.button1, "yo mama");
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("Required view '<unavailable while editing>' "
          + "with ID " + android.R.id.button1
          + " for yo mama was not found. If this view is optional add '@Nullable' (fields) or '@Optional' (methods) annotation.");
    }
  }
}
