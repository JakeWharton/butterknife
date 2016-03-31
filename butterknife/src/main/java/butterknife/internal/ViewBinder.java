package butterknife.internal;

import butterknife.ButterKnife.Unbinder;

public interface ViewBinder<T> {
  Unbinder<?> bind(Finder finder, T target, Object source);
}
