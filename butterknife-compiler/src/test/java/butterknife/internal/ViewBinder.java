package butterknife.internal;

import android.view.View;
import butterknife.Unbinder;

public interface ViewBinder<T> {
  Unbinder bind(T target, View source);
}
