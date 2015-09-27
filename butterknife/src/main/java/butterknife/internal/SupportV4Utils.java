package butterknife.internal;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;

import static butterknife.internal.CompatUtils.getColor;
import static butterknife.internal.CompatUtils.getDrawable;

/** Requires v4 Support Library. Check {@link #HAS_SUPPORT_V4_LIBRARY} before use. */
final class SupportV4Utils {
  static final boolean HAS_SUPPORT_V4_LIBRARY = hasSupportV4LibraryOnClasspath();

  private SupportV4Utils() {
    throw new AssertionError("No instances.");
  }

  static Drawable setDrawableTint(Resources res, Resources.Theme theme, int drawableId,
      int attributeId) {
    TypedValue outValue = new TypedValue();
    boolean attributeFound = theme.resolveAttribute(attributeId, outValue, true);
    if (!attributeFound) {
      throw new Resources.NotFoundException("Tint attribute not found.");
    }

    Drawable drawable = getDrawable(theme, res, drawableId);
    drawable = DrawableCompat.wrap(drawable.mutate());
    int color = getColor(theme, res, outValue.resourceId);
    DrawableCompat.setTint(drawable, color);
    return drawable;
  }

  private static boolean hasSupportV4LibraryOnClasspath() {
    try {
      Class.forName("android.support.v4.graphics.drawable.DrawableCompat");
      return true;
    } catch (ClassNotFoundException ignored) {
      return false;
    }
  }
}
