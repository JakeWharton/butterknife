package butterknife.internal;

import android.view.View;

/**
 * DO NOT MODIFY! This is a Stub class for DebouncingOnClickListener.java found in the butterknife
 * module, in order to work around the fact that java projects can't depend on android libraries.
 */
public abstract class DebouncingOnClickListener implements View.OnClickListener {

  static boolean enabled = true;

  private static final Runnable ENABLE_AGAIN = new Runnable() {
    @Override public void run() {
      enabled = true;
    }
  };

  @Override public final void onClick(View v) {
    throw new RuntimeException("Stub!");
  }

  public abstract void doClick(View v);
}
