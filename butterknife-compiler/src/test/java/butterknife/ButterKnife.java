package butterknife;

import android.content.Context;
import android.view.View;
import java.util.List;

/**
 * This is a stub class for ButterKnife.java found in the butterknife module, in order to work
 * around the fact that Java projects can't depend on Android libraries.
 */
public final class ButterKnife {

  public static class Finder {

    private static <T> T[] filterNull(T[] views) {
      throw new RuntimeException("Stub!");
    }

    public static <T> T[] arrayOf(T... views) {
      throw new RuntimeException("Stub!");
    }

    public static <T> List<T> listOf(T... views) {
      throw new RuntimeException("Stub!");
    }

    public <T> T findRequiredView(Object source, int id, String who) {
      throw new RuntimeException("Stub!");
    }

    public <T> T findOptionalView(Object source, int id, String who) {
      throw new RuntimeException("Stub!");
    }

    @SuppressWarnings("unchecked") // That's the point.
    public <T> T castView(View view, int id, String who) {
      throw new RuntimeException("Stub!");
    }

    @SuppressWarnings("unchecked") // That's the point.
    public <T> T castParam(Object value, String from, int fromPosition, String to, int toPosition) {
      throw new RuntimeException("Stub!");
    }

    public Context getContext(Object source) {
      throw new RuntimeException("Stub!");
    }
  }

  public interface ViewBinder<T> {
    void bind(Finder finder, T target, Object source);

    void unbind(T target);
  }
}
