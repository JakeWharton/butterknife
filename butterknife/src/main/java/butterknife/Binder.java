package butterknife;

import android.support.annotation.UiThread;

/** An unbinder contract that will unbind views when called. */
public interface Binder {
    @UiThread
    <V,D>void apply(V bean,D data);
    class Default implements Binder{
      @Override
      public <V, D> void apply(V bean, D data) {
        
      }
    }
}
