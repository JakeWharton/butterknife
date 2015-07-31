package butterknife.internal;

/**
 * Contains shared constants between the annotation processor and {@link butterknife.ButterKnife}
 */
public final class InternalKeys {
  public static final String BINDING_CLASS_SUFFIX = "$$ViewBinder";
  public static final String ANDROID_PREFIX = "android.";
  public static final String JAVA_PREFIX = "java.";

  /** Prevent class from instantiation (aka being paranoid) */
  private InternalKeys() {
    throw new AssertionError("InternalKeys is a constant container. Should not be instantiated");
  }
}
