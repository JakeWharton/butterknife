package butterknife.internal;

import java.lang.reflect.Array;

/**
 * Exposes/reimplements select {@linkplain java.util.Arrays Arrays} methods not supported on API
 * versions below 9.
 */
public class ArraysCompat {
  static public <T> T[] copyOfRange(T[] original, int from, int to) {
    return copyOfRange(original, from, to, (Class<T[]>) original.getClass());
  }

  @SuppressWarnings("unchecked")
  public static <T, U> T[] copyOfRange(U[] original, int from, int to,
                                       Class<? extends T[]> newType) {
    int newLength = to - from;
    if (newLength < 0)
      throw new IllegalArgumentException(from + " > " + to);
    T[] copy = ((Object) newType == (Object) Object[].class)
        ? (T[]) new Object[newLength]
        : (T[]) Array.newInstance(newType.getComponentType(), newLength);
    System.arraycopy(original, from, copy, 0,
        Math.min(original.length - from, newLength));
    return copy;
  }

  private ArraysCompat() {
    // no instances
  }
}