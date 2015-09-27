package butterknife.internal;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

final class CompatUtils {

  private CompatUtils() {
    throw new AssertionError("No instances.");
  }

  static int getColor(Resources.Theme theme, Resources res, int colorId) {
    int color;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      color = res.getColor(colorId, theme);
    } else {
      //noinspection deprecation
      color = res.getColor(colorId);
    }
    return color;
  }

  static Drawable getDrawable(Resources.Theme theme, Resources res, int drawableId) {
    Drawable drawable;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      drawable = res.getDrawable(drawableId, theme);
    } else {
      //noinspection deprecation
      drawable = res.getDrawable(drawableId);
    }
    return drawable;
  }
}
