package butterknife.internal;

import android.view.View;

/**
 * A {@link View.OnClickListener} that enables debouncing of multiple clicks posted in a row.
 *
 * Once a click is fired, a post is enqueued to the main thread looper queue and no further click
 * is allowed until that post is dequeued.
 *
 * A click on one button disables all buttons.
 *
 */
public abstract class DebouncedOnClickListener implements View.OnClickListener {

  /**
   * This is static because we want to disable clicks for all click listeners.
   */
  private static boolean enabled = true;

  private static final Runnable ENABLE_AGAIN = new Runnable() {
    @Override public void run() {
      enabled = true;
    }
  };

  @Override public final void onClick(View v) {
    if (enabled) {
      enabled = false;
      v.post(ENABLE_AGAIN);
      doClick(v);
    }
  }

  public abstract void doClick(View v);
}
