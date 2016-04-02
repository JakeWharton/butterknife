package butterknife;

/** An unbinder contract that will unbind views when called. */
public interface Unbinder {
  void unbind();

  Unbinder EMPTY = new Unbinder() {
    @Override public void unbind() { }
  };
}
