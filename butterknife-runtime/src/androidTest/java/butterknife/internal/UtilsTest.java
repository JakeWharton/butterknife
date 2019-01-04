package butterknife.internal;

import android.content.Context;
import android.view.View;
import androidx.test.InstrumentationRegistry;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class UtilsTest {
  @Test public void finderThrowsNiceError() {
    Context context = InstrumentationRegistry.getContext();
    View view = new View(context);
    try {
      Utils.findRequiredView(view, android.R.id.button1, "yo mama");
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("Required view 'button1' with ID "
          + android.R.id.button1
          + " for yo mama was not found. If this view is optional add '@Nullable' (fields) or '@Optional' (methods) annotation.");
    }
  }

  @Test public void finderThrowsLessNiceErrorInEditMode() {
    Context context = InstrumentationRegistry.getContext();
    View view = new EditModeView(context);
    try {
      Utils.findRequiredView(view, android.R.id.button1, "yo mama");
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("Required view '<unavailable while editing>' "
          + "with ID " + android.R.id.button1
          + " for yo mama was not found. If this view is optional add '@Nullable' (fields) or '@Optional' (methods) annotation.");
    }
  }

  static final class EditModeView extends View {
    EditModeView(Context context) {
      super(context);
    }

    @Override public boolean isInEditMode() {
      return true;
    }
  }
}
