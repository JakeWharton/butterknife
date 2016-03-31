package butterknife;

import android.view.View;

/** STUB! Required for test sources to compile. */
public class ButterKnife {
  public interface ViewUnbinder<T> {
    Unbinder unbind();
  }

  public static final Unbinder NOP_UNBINDER = new Unbinder() {
    @Override
    public void unbind() { }
  };

  public static Unbinder bind(Object target, View view) {
    return NOP_UNBINDER;
  }
}
