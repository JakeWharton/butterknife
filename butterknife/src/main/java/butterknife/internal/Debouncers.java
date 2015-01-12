package butterknife.internal;

/**
 * Static flag for debouncing input events, and a {@link Runnable} to re-enable them.
 */
class Debouncers {
  static boolean enabled = true;

  static final Runnable ENABLE_AGAIN = new Runnable() {
    @Override public void run() {
      enabled = true;
    }
  };

  private Debouncers() {
  }
}
