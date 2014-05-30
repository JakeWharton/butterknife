package butterknife;

import android.view.View;
import java.util.AbstractList;
import java.util.RandomAccess;

/**
 * An immutable list of views which is lighter than {@code
 * Collections.unmodifiableList(new ArrayList<>(Arrays.asList(foo, bar)))}.
 */
final class ImmutableViewList<T extends View> extends AbstractList<T> implements RandomAccess {
  private final T[] views;

  ImmutableViewList(T[] views) {
    this.views = views;
  }

  @Override public T get(int index) {
    return views[index];
  }

  @Override public int size() {
    return views.length;
  }

  @Override public boolean contains(Object o) {
    for (View view : views) {
      if (view == o) {
        return true;
      }
    }
    return false;
  }
}
