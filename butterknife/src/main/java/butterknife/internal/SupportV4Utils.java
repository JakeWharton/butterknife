package butterknife.internal;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;

import static butterknife.internal.CompatUtils.getColor;

/** Requires v4 Support Library. Check {@link #HAS_SUPPORT_V4_LIBRARY} before use. */
final class SupportV4Utils {
  static final boolean HAS_SUPPORT_V4_LIBRARY = hasSupportV4LibraryOnClasspath();
  static final TypedValue OUT_VALUE = new TypedValue();

  private SupportV4Utils() {
    throw new AssertionError("No instances.");
  }

  static Drawable getTintedDrawable(Resources res, int drawableId, int tintAttributeId,
      Resources.Theme theme) {
    boolean attributeFound = theme.resolveAttribute(tintAttributeId, OUT_VALUE, true);
    if (!attributeFound) {
      throw new Resources.NotFoundException("Required tint color attribute with name "
          + res.getResourceEntryName(tintAttributeId)
          + " and attribute ID "
          + tintAttributeId
          + " was not found.");
    }

    Drawable drawable = CompatUtils.getDrawable(res, drawableId, theme);
    drawable = DrawableCompat.wrap(drawable.mutate());
    int color = getColor(res, OUT_VALUE.resourceId, theme);
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
