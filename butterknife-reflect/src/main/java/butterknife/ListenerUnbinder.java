package butterknife;

import android.view.View;
import java.util.List;

final class ListenerUnbinder<V extends View, L> implements Unbinder {
  private final List<V> targets;
  private final Setter<V, L> setter;
  private final L listener;

  ListenerUnbinder(List<V> targets, Setter<V, L> setter) {
    this.targets = targets;
    this.setter = setter;
    this.listener = null;
  }

  ListenerUnbinder(List<V> targets, Setter<V, L> setter, L listener) {
    this.targets = targets;
    this.setter = setter;
    this.listener = listener;
  }

  @Override public void unbind() {
    ViewCollections.set(targets, setter, listener);
  }
}
