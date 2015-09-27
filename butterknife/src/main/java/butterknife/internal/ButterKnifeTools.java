package butterknife.internal;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/** DO NOT USE: Exposed for generated code. */
@SuppressWarnings("UnusedDeclaration") // Used by generated code.
public final class ButterKnifeTools {
  private ButterKnifeTools() {
    throw new AssertionError("No instances.");
  }

  public static Drawable setDrawableTint(Resources res, Resources.Theme theme, int drawableId,
      int attributeId) {
    if (SupportV4Utils.HAS_SUPPORT_V4_LIBRARY) {
      return SupportV4Utils.setDrawableTint(res, theme, drawableId, attributeId);
    } else {
      throw new RuntimeException("Android v4 Support Library is required for @BindDrawable with " +
          "tint.");
    }
  }
}
