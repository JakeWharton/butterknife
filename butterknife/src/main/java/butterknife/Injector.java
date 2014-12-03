package butterknife;

import static butterknife.ButterKnife.Finder;

/**
 * An interface for all generated ViewInjector classes
 *
 * @param <T> the type of target to be injected
 */
public interface Injector<T> {
  void inject(Finder finder, T target, Object source);
  void reset(T target);
}
