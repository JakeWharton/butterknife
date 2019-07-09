package butterknife;

import android.util.Property;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import java.util.List;

/** Convenience methods for working with view collections. */
public final class ViewCollections {
  /** Apply the specified {@code actions} across the {@code list} of views. */
  @UiThread
  @SafeVarargs public static <T extends View> void run(@NonNull List<T> list,
      @NonNull Action<? super T>... actions) {
    for (int i = 0, count = list.size(); i < count; i++) {
      for (Action<? super T> action : actions) {
        action.apply(list.get(i), i);
      }
    }
  }

  /** Apply the specified {@code actions} across the {@code array} of views. */
  @UiThread
  @SafeVarargs public static <T extends View> void run(@NonNull T[] array,
      @NonNull Action<? super T>... actions) {
    for (int i = 0, count = array.length; i < count; i++) {
      for (Action<? super T> action : actions) {
        action.apply(array[i], i);
      }
    }
  }

  /** Apply the specified {@code action} across the {@code list} of views. */
  @UiThread
  public static <T extends View> void run(@NonNull List<T> list,
      @NonNull Action<? super T> action) {
    for (int i = 0, count = list.size(); i < count; i++) {
      action.apply(list.get(i), i);
    }
  }

  /** Apply the specified {@code action} across the {@code array} of views. */
  @UiThread
  public static <T extends View> void run(@NonNull T[] array, @NonNull Action<? super T> action) {
    for (int i = 0, count = array.length; i < count; i++) {
      action.apply(array[i], i);
    }
  }

  /** Apply {@code actions} to {@code view}. */
  @UiThread
  @SafeVarargs public static <T extends View> void run(@NonNull T view,
      @NonNull Action<? super T>... actions) {
    for (Action<? super T> action : actions) {
      action.apply(view, 0);
    }
  }

  /** Apply {@code action} to {@code view}. */
  @UiThread
  public static <T extends View> void run(@NonNull T view, @NonNull Action<? super T> action) {
    action.apply(view, 0);
  }

  /** Set the {@code value} using the specified {@code setter} across the {@code list} of views. */
  @UiThread
  public static <T extends View, V> void set(@NonNull List<T> list,
      @NonNull Setter<? super T, V> setter, @Nullable V value) {
    for (int i = 0, count = list.size(); i < count; i++) {
      setter.set(list.get(i), value, i);
    }
  }

  /** Set the {@code value} using the specified {@code setter} across the {@code array} of views. */
  @UiThread
  public static <T extends View, V> void set(@NonNull T[] array,
      @NonNull Setter<? super T, V> setter, @Nullable V value) {
    for (int i = 0, count = array.length; i < count; i++) {
      setter.set(array[i], value, i);
    }
  }

  /** Set {@code value} on {@code view} using {@code setter}. */
  @UiThread
  public static <T extends View, V> void set(@NonNull T view,
      @NonNull Setter<? super T, V> setter, @Nullable V value) {
    setter.set(view, value, 0);
  }

  /**
   * Apply the specified {@code value} across the {@code list} of views using the {@code property}.
   */
  @UiThread
  public static <T extends View, V> void set(@NonNull List<T> list,
      @NonNull Property<? super T, V> setter, @Nullable V value) {
    //noinspection ForLoopReplaceableByForEach
    for (int i = 0, count = list.size(); i < count; i++) {
      setter.set(list.get(i), value);
    }
  }

  /**
   * Apply the specified {@code value} across the {@code array} of views using the {@code property}.
   */
  @UiThread
  public static <T extends View, V> void set(@NonNull T[] array,
      @NonNull Property<? super T, V> setter, @Nullable V value) {
    //noinspection ForLoopReplaceableByForEach
    for (int i = 0, count = array.length; i < count; i++) {
      setter.set(array[i], value);
    }
  }

  /** Apply {@code value} to {@code view} using {@code property}. */
  @UiThread
  public static <T extends View, V> void set(@NonNull T view,
      @NonNull Property<? super T, V> setter, @Nullable V value) {
    setter.set(view, value);
  }

  private ViewCollections() {
  }
}
