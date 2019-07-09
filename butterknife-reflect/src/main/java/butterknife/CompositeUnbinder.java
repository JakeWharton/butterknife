package butterknife;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

final class CompositeUnbinder implements Unbinder {
  private @Nullable List<Unbinder> unbinders;

  CompositeUnbinder(@NonNull List<Unbinder> unbinders) {
    this.unbinders = unbinders;
  }

  @Override public void unbind() {
    if (unbinders == null) {
      throw new IllegalStateException("Bindings already cleared.");
    }
    for (Unbinder unbinder : unbinders) {
      unbinder.unbind();
    }
    unbinders = null;
  }
}
