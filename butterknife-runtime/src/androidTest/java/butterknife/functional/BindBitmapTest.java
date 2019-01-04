package butterknife.functional;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.test.InstrumentationRegistry;
import butterknife.BindBitmap;
import butterknife.Unbinder;
import butterknife.runtime.test.R;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public final class BindBitmapTest {
  private final Context context = InstrumentationRegistry.getContext();

  static class Target {
    @BindBitmap(R.drawable.pixel) Bitmap actual;
  }

  @Test public void asBitmap() {
    Target target = new Target();
    Bitmap expected = BitmapFactory.decodeResource(context.getResources(), R.drawable.pixel);

    Unbinder unbinder = new BindBitmapTest$Target_ViewBinding(target, context);
    assertTrue(target.actual.sameAs(expected));

    unbinder.unbind();
    assertTrue(target.actual.sameAs(expected));
  }
}
