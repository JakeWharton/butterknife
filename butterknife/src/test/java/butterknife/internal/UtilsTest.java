package butterknife.internal;

import android.view.View;
import butterknife.shadow.EditModeShadowView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static butterknife.internal.Utils.arrayOf;
import static butterknife.internal.Utils.listOf;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public final class UtilsTest {
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
    assertThat(arrayOf("One", null, null)).asList().containsExactly("One");
    assertThat(arrayOf(null, "One", null)).asList().containsExactly("One");
    assertThat(arrayOf(null, null, "One")).asList().containsExactly("One");
    assertThat(arrayOf("One", "Two", null)).asList().containsExactly("One", "Two");
    assertThat(arrayOf("One", null, "Two")).asList().containsExactly("One", "Two");
    assertThat(arrayOf(null, "One", "Two")).asList().containsExactly("One", "Two");
  }

  @Test public void finderThrowsNiceError() {
    View view = new View(RuntimeEnvironment.application);
    try {
      Utils.findRequiredView(view, android.R.id.button1, "yo mama");
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
      Utils.findRequiredView(view, android.R.id.button1, "yo mama");
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("Required view '<unavailable while editing>' "
          + "with ID " + android.R.id.button1
          + " for yo mama was not found. If this view is optional add '@Nullable' (fields) or '@Optional' (methods) annotation.");
    }
  }
}
