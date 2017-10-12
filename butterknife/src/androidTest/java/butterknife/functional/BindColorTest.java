package butterknife.functional;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import butterknife.BindColor;
import butterknife.Unbinder;
import butterknife.test.R;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class BindColorTest {
  private final Context context = InstrumentationRegistry.getContext();

  static class Target {
    @BindColor(R.color.red) int actual;
  }

  @Test public void asInt() {
    Target target = new Target();
    int expected = context.getResources().getColor(R.color.red);

    Unbinder unbinder = new BindColorTest$Target_ViewBinding(target, context);
    assertThat(target.actual).isEqualTo(expected);

    unbinder.unbind();
    assertThat(target.actual).isEqualTo(expected);
  }
}
