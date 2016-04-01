package butterknife;

/** STUB! Required for test sources to compile. */
public interface Unbinder {
  void unbind();

  Unbinder EMPTY = new Unbinder() {
    @Override public void unbind() { }
  };
}
