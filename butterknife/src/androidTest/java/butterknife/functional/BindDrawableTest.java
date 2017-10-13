package butterknife.functional;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.test.InstrumentationRegistry;
import butterknife.BindBitmap;
import butterknife.BindDrawable;
import butterknife.Unbinder;
import butterknife.test.R;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertTrue;

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
