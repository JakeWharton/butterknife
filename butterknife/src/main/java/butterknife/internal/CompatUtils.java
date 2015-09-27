package butterknife.internal;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

final class CompatUtils {
  private CompatUtils() {
    throw new AssertionError("No instances.");
  }

  static int getColor(Resources res, int colorId, Resources.Theme theme) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return res.getColor(colorId, theme);
    } else {
      //noinspection deprecation
      return res.getColor(colorId);
    }
  }

  static Drawable getDrawable(Resources res, int drawableId, Resources.Theme theme) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      return res.getDrawable(drawableId, theme);
    } else {
      //noinspection deprecation
      return res.getDrawable(drawableId);
    }
  }
}
