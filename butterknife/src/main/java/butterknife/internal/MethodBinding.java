package butterknife.internal;

class MethodBinding implements Binding {
  private final String name;
  private final String type;
  private final boolean required;

  MethodBinding(String name, String type, boolean required) {
    this.name = name;
    this.type = type;
    this.required = required;
  }

  @Override public String getName() {
    return name;
  }

  @Override public String getDescription() {
    return "method '" + name + "'";
  }

  @Override public String getViewType() {
    return type;
  }

  @Override public boolean isRequired() {
    return required;
  }
}
