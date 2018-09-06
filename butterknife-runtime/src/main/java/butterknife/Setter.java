package butterknife;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.View;

/** A setter that can apply a value to a list of views. */
public interface Setter<T extends View, V> {
  /** Set the {@code value} on the {@code view} which is at {@code index} in the list. */
  @UiThread void set(@NonNull T view, V value, int index);
}
