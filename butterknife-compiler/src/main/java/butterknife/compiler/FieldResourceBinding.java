package butterknife.compiler;

final class FieldResourceBinding {
  private final Id id;
  private final String name;
  private final String method;
  private final boolean themeable;

  FieldResourceBinding(Id id, String name, String method, boolean themeable) {
    this.id = id;
    this.name = name;
    this.method = method;
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

  public boolean isThemeable() {
    return themeable;
  }
}
