package butterknife.internal;

import java.util.Arrays;
import java.util.List;

public final class Utils {
  private static <T> T[] filterNull(T[] views) {
    int end = 0;
    for (int i = 0; i < views.length; i++) {
      T view = views[i];
      if (view != null) {
        views[end++] = view;
      }
    }
    return Arrays.copyOfRange(views, 0, end);
  }

  @SafeVarargs
  public static <T> T[] arrayOf(T... views) {
    return filterNull(views);
  }

  @SafeVarargs
  public static <T> List<T> listOf(T... views) {
    return new ImmutableList<>(filterNull(views));
  }

  private Utils() {
    throw new AssertionError("No instances.");
  }
}
