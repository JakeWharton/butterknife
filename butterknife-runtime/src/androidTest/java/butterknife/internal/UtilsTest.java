package butterknife.internal;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.view.View;
import org.junit.Test;

import static butterknife.internal.Utils.arrayFilteringNull;
import static butterknife.internal.Utils.listFilteringNull;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class UtilsTest {
  @Test public void listOfFiltersNull() {
    assertThat(listFilteringNull(null, null, null)).isEmpty();
    assertThat(listFilteringNull("One", null, null)).containsExactly("One");
    assertThat(listFilteringNull(null, "One", null)).containsExactly("One");
    assertThat(listFilteringNull(null, null, "One")).containsExactly("One");
    assertThat(listFilteringNull("One", "Two", null)).containsExactly("One", "Two");
    assertThat(listFilteringNull("One", null, "Two")).containsExactly("One", "Two");
    assertThat(listFilteringNull(null, "One", "Two")).containsExactly("One", "Two");
  }

  @Test public void arrayOfFiltersNull() {
    assertThat(arrayFilteringNull(null, null, null)).isEmpty();
    assertThat(arrayFilteringNull("One", null, null)).asList().containsExactly("One");
    assertThat(arrayFilteringNull(null, "One", null)).asList().containsExactly("One");
    assertThat(arrayFilteringNull(null, null, "One")).asList().containsExactly("One");
    assertThat(arrayFilteringNull("One", "Two", null)).asList().containsExactly("One", "Two");
    assertThat(arrayFilteringNull("One", null, "Two")).asList().containsExactly("One", "Two");
    assertThat(arrayFilteringNull(null, "One", "Two")).asList().containsExactly("One", "Two");
  }

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

  @Test public void testCastParam() {
    try {
      Utils.castParam("abc", "Foo", 3, "foo()", 4, Integer.class);
      fail();
    } catch (IllegalStateException ise) {
      assertThat(ise.getMessage()).isEqualTo(
          "Parameter #4 of method 'Foo' was of the wrong type for parameter #5 of method 'foo()'. See cause for more info.");
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
