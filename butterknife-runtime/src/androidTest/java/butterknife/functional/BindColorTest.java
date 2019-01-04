package butterknife.functional;

import android.content.Context;
import android.content.res.ColorStateList;
import androidx.test.InstrumentationRegistry;
import butterknife.BindColor;
import butterknife.Unbinder;
import butterknife.runtime.test.R;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class BindColorTest {
  private final Context context = InstrumentationRegistry.getContext();

  static class IntTarget {
    @BindColor(R.color.red) int actual;
  }

  @Test public void asInt() {
    IntTarget target = new IntTarget();
    int expected = context.getResources().getColor(R.color.red);

    Unbinder unbinder = new BindColorTest$IntTarget_ViewBinding(target, context);
    assertThat(target.actual).isEqualTo(expected);

    unbinder.unbind();
    assertThat(target.actual).isEqualTo(expected);
  }

  static class ColorStateListTarget {
    @BindColor(R.color.colors) ColorStateList actual;
  }

  @Test public void asColorStateList() {
    ColorStateListTarget target = new ColorStateListTarget();
    ColorStateList expected = context.getResources().getColorStateList(R.color.colors);

    Unbinder unbinder = new BindColorTest$ColorStateListTarget_ViewBinding(target, context);
    assertThat(target.actual.toString()).isEqualTo(expected.toString());

    unbinder.unbind();
    assertThat(target.actual.toString()).isEqualTo(expected.toString());
  }
}
