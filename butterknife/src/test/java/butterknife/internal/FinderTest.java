package butterknife.internal;

import android.view.View;
import butterknife.shadow.EditModeShadowView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public final class FinderTest {
  @Test public void finderThrowsNiceError() {
    View view = new View(Robolectric.application);
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
    View view = new View(Robolectric.application);
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
