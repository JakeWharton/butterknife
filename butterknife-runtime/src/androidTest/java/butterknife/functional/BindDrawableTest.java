package butterknife.functional;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.test.InstrumentationRegistry;
import butterknife.BindDrawable;
import butterknife.Unbinder;
import butterknife.runtime.test.R;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class BindDrawableTest {
  private final Context context = InstrumentationRegistry.getContext();

  static class Target {
    @BindDrawable(R.drawable.circle) Drawable actual;
  }

  @Test public void asDrawable() {
    Target target = new Target();
    Drawable expected = context.getResources().getDrawable(R.drawable.circle);

    Unbinder unbinder = new BindDrawableTest$Target_ViewBinding(target, context);
    assertThat(target.actual.getConstantState()).isEqualTo(expected.getConstantState());

    unbinder.unbind();
    assertThat(target.actual.getConstantState()).isEqualTo(expected.getConstantState());
  }
}
