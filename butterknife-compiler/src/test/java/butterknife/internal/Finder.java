package butterknife.internal;

import android.content.Context;
import android.view.View;

public class Finder {

  public View findRequiredView(Object source, int id, String who) {
    throw new RuntimeException("Stub!");
  }

  public <T> T findRequiredViewAsType(Object source, int id, String who, Class<T> cls) {
    throw new RuntimeException("Stub!");
  }

  public View findOptionalView(Object source, int id) {
    throw new RuntimeException("Stub!");
  }

  public <T> T findOptionalViewAsType(Object source, int id, String who, Class<T> cls) {
    throw new RuntimeException("Stub!");
  }

  public <T> T castView(View view, int id, String who) {
    throw new RuntimeException("Stub!");
  }

  public <T> T castParam(Object value, String from, int fromPosition, String to, int toPosition) {
    throw new RuntimeException("Stub!");
  }

  public Context getContext(Object source) {
    throw new RuntimeException("Stub!");
  }
}
