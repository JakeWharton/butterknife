package butterknife.internal;

final class ResourceBinding implements Binding {
  private final String name;
  private final String type;
  private final String getter;

  ResourceBinding(String name, String type, String getter) {
    this.name = name;
    this.type = type;
    this.getter = getter;
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

  public String getGetter() {
    return getter;
  }
}
