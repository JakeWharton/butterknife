package butterknife;

import android.support.annotation.UiThread;

/** An unbinder contract that will unbind views when called. */
public interface Unbinder<T> {
  @UiThread void unbind();
  void apply(T bean);
  Unbinder EMPTY = new Unbinder<Void>() {
    @Override public void unbind() { }
    @Override
    public  void apply(Void bean) {

    }
  };
}
