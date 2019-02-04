package butterknife;

import android.view.View;
import java.util.List;

final class ListenerUnbinder<T extends View, V> implements Unbinder {
  private final List<T> targets;
  private final Setter<T, V> setter;
  private final V listener;

  ListenerUnbinder(List<T> targets, Setter<T, V> setter) {
    this.targets = targets;
    this.setter = setter;
    this.listener = null;
  }

  ListenerUnbinder(List<T> targets, Setter<T, V> setter, V listener) {
    this.targets = targets;
    this.setter = setter;
    this.listener = listener;
  }

  @Override public void unbind() {
    ViewCollections.set(targets, setter, listener);
  }
}
