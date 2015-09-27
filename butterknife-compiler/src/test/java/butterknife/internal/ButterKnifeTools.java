package butterknife.internal;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * This is a stub class for ButterKnifeTools.java found in the butterknife module, in order to work
 * around the fact that Java projects can't depend on Android libraries.
 */
public final class ButterKnifeTools {
  private ButterKnifeTools() {
    throw new AssertionError("No instances.");
  }

  public static Drawable setDrawableTint(Resources res, Resources.Theme theme, int drawableId,
      int attributeId) {
    throw new RuntimeException("Stub!");
  }
}
