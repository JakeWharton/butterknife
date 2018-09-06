package butterknife;

import java.util.List;

final class CompositeUnbinder implements Unbinder {
  private List<Unbinder> unbinders;

  CompositeUnbinder(List<Unbinder> unbinders) {
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
