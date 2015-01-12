package butterknife.internal;

import android.text.Editable;
import android.text.TextWatcher;

import static butterknife.internal.Debouncers.enabled;

/**
 * A {@linkplain TextWatcher text watcher} that debounces text events posted in the same frame
 * as a button click.
 */
public class DebouncingTextWatcher implements TextWatcher {
  @Override public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
    if (enabled) {
      doBeforeTextChanged(s, start, count, after);
    }
  }

  @Override public final void onTextChanged(CharSequence s, int start, int before, int count) {
    if (enabled) {
      doTextChanged(s, start, before, count);
    }
  }

  @Override public final void afterTextChanged(Editable s) {
    if (enabled) {
      doAfterTextChanged(s);
    }
  }

  public void doBeforeTextChanged(CharSequence s, int start, int count, int after) {
  }

  public void doTextChanged(CharSequence s, int start, int before, int count) {
  }

  public void doAfterTextChanged(Editable s) {
  }
}
