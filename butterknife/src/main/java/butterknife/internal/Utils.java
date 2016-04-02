package butterknife.internal;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;
import java.lang.reflect.Array;
import java.util.List;

@SuppressWarnings("deprecation") //
public final class Utils {
  private static final boolean HAS_SUPPORT_V4 = hasSupportV4();

  private static boolean hasSupportV4() {
    try {
      Class.forName("android.support.v4.graphics.drawable.DrawableCompat");
      return true;
    } catch (ClassNotFoundException ignored) {
      return false;
    } catch (VerifyError ignored) {
      return false;
    }
  }

  public static Drawable getTintedDrawable(Resources res, Resources.Theme theme,
      @DrawableRes int id, @AttrRes int tintAttrId) {
    if (HAS_SUPPORT_V4) {
      return SupportV4.getTintedDrawable(res, theme, id, tintAttrId);
    }
    throw new RuntimeException(
        "Android support-v4 library is required for @BindDrawable with tint.");
  }

  public static int getColor(Resources res, Resources.Theme theme, @ColorRes int id) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return res.getColor(id);
    }
    return res.getColor(id, theme);
  }

  public static ColorStateList getColorStateList(Resources res, Resources.Theme theme,
      @ColorRes int id) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return res.getColorStateList(id);
    }
    return res.getColorStateList(id, theme);
  }

  public static Drawable getDrawable(Resources res, Resources.Theme theme, @DrawableRes int id) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return res.getDrawable(id);
    }
    return res.getDrawable(id, theme);
  }

  @SafeVarargs
  public static <T> T[] arrayOf(T... views) {
    return filterNull(views);
  }

  @SafeVarargs
  public static <T> List<T> listOf(T... views) {
    return new ImmutableList<>(filterNull(views));
  }

  private static <T> T[] filterNull(T[] views) {
    int end = 0;
    int length = views.length;
    for (int i = 0; i < length; i++) {
      T view = views[i];
      if (view != null) {
        views[end++] = view;
      }
    }
    if (end == length) {
      return views;
    }
    //noinspection unchecked
    T[] newViews = (T[]) Array.newInstance(views.getClass().getComponentType(), end);
    System.arraycopy(views, 0, newViews, 0, end);
    return newViews;
  }

  static class SupportV4 {
    private static final TypedValue OUT_VALUE = new TypedValue();

    static Drawable getTintedDrawable(Resources res, Resources.Theme theme, @DrawableRes int id,
        @AttrRes int tintAttributeId) {
      boolean attributeFound = theme.resolveAttribute(tintAttributeId, OUT_VALUE, true);
      if (!attributeFound) {
        throw new Resources.NotFoundException("Required tint color attribute with name "
            + res.getResourceEntryName(tintAttributeId)
            + " and attribute ID "
            + tintAttributeId
            + " was not found.");
      }

      Drawable drawable = getDrawable(res, theme, id);
      drawable = DrawableCompat.wrap(drawable.mutate());
      int color = getColor(res, theme, OUT_VALUE.resourceId);
      DrawableCompat.setTint(drawable, color);
      return drawable;
    }
  }

  private Utils() {
    throw new AssertionError("No instances.");
  }
}
