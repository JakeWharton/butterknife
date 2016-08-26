package butterknife.compiler;

final class FieldResourceBinding {
  private final Id id;
  private final String name;
  private final String method;
  private final boolean requiresUtils;
  private final boolean themeable;

  FieldResourceBinding(Id id, String name, String method, boolean requiresUtils,
      boolean themeable) {
    if (themeable && !requiresUtils) {
      throw new IllegalArgumentException("Being themeable must require utils.");
    }
    this.id = id;
    this.name = name;
    this.method = method;
    this.requiresUtils = requiresUtils;
    this.themeable = themeable;
  }

  public Id getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getMethod() {
    return method;
  }

  public boolean requiresUtils() {
    return requiresUtils;
  }

  public boolean isThemeable() {
    return themeable;
  }
}
