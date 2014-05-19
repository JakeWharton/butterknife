package butterknife.internal;

final class ResourceBinding implements Binding {
  private final String name;
  private final String type;

  ResourceBinding(String name, String type) {
    this.name = name;
    this.type = type;
  }

  @Override
  public String getDescription() {
    return "field '" + name + "'";
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }
}
