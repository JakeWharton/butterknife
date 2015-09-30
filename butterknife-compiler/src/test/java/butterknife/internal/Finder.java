package butterknife.internal;

import android.content.Context;
import android.view.View;
import java.util.List;

public class Finder {

  private static <T> T[] filterNull(T[] views) {
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
