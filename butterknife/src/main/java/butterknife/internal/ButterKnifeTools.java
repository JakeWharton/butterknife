package butterknife.internal;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/** DO NOT USE: Exposed for generated code. */
@SuppressWarnings("UnusedDeclaration") // Used by generated code.
public final class ButterKnifeTools {
  private ButterKnifeTools() {
    throw new AssertionError("No instances.");
  }

  public static Drawable getDrawable(Resources res, int drawableId, int tintAttributeId,
      Resources.Theme theme) {
    if (tintAttributeId == 0) {
      return CompatUtils.getDrawable(res, drawableId, theme);
    } else if (SupportV4Utils.HAS_SUPPORT_V4_LIBRARY) {
      return SupportV4Utils.getTintedDrawable(res, drawableId, tintAttributeId, theme);
    } else {
      throw new RuntimeException("Android v4 Support Library is required for @BindDrawable with "
          + "tint.");
    }
  }
}
