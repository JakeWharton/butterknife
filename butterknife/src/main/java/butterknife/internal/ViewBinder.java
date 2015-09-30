package butterknife.internal;

public interface ViewBinder<T> {
  void bind(Finder finder, T target, Object source);
  void unbind(T target);
}
