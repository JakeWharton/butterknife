package butterknife;

import android.view.View;
import java.util.List;

final class ListenerUnbinder<T extends View> implements Unbinder {
  private final List<T> targets;
  private final Setter<T, ?> setter;

  ListenerUnbinder(List<T> targets, Setter<T, ?> setter) {
    this.targets = targets;
    this.setter = setter;
  }

  @Override public void unbind() {
    ViewCollections.set(targets, setter, null);
  }
}
