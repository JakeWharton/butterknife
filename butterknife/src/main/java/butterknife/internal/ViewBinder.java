package butterknife.internal;

import butterknife.ButterKnife.ViewUnbinder;

public interface ViewBinder<T> {
  ViewUnbinder<?> bind(Finder finder, T target, Object source);
}
