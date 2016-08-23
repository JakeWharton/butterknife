package butterknife.internal;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import java.util.List;

public final class Utils {
  public static Drawable getTintedDrawable(Resources res, Resources.Theme theme, int id,
      int tintAttrId) {
    throw new RuntimeException("Stub!");
  }

  public static int getColor(Resources res, Resources.Theme theme, int id) {
    throw new RuntimeException("Stub!");
  }

  public static ColorStateList getColorStateList(Resources res, Resources.Theme theme, int id) {
    throw new RuntimeException("Stub!");
  }

  public static Drawable getDrawable(Resources res, Resources.Theme theme, int id) {
    throw new RuntimeException("Stub!");
  }

  @SafeVarargs
  public static <T> T[] arrayOf(T... views) {
    throw new RuntimeException("Stub!");
  }

  @SafeVarargs
  public static <T> List<T> listOf(T... views) {
    throw new RuntimeException("Stub!");
  }

  public static View findRequiredView(View source, int id, String who) {
    throw new RuntimeException("Stub!");
  }

  public static <T> T findRequiredViewAsType(View source, int id, String who, Class<T> cls) {
    throw new RuntimeException("Stub!");
  }

  public static <T> T findOptionalViewAsType(View source, int id, String who, Class<T> cls) {
    throw new RuntimeException("Stub!");
  }

  public static <T> T castView(View view, int id, String who, Class<T> cls) {
    throw new RuntimeException("Stub!");
  }

  public static <T> T castParam(Object value, String from, int fromPosition, String to, int toPosition) {
    throw new RuntimeException("Stub!");
  }
}
