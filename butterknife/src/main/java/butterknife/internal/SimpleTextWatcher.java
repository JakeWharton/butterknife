package butterknife.internal;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class SimpleTextWatcher implements TextWatcher {
  @Override public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
  }

  @Override public void onTextChanged(CharSequence sequence, int start, int before, int count) {
  }

  @Override public void afterTextChanged(Editable editable) {
  }
}
