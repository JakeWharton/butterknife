package butterknife.internal;

class MethodBinding implements Binding {
  private final String name;
  private final String annotation;
  private final Param[] params;
  private final boolean required;

  MethodBinding(String name, String annotation, Param[] params, boolean required) {
    this.name = name;
    this.annotation = annotation;
    this.params = params;
    this.required = required;
  }

  public String getName() {
    return name;
  }

  public String getAnnotation() {
    return annotation;
  }

  public Param[] getParams() {
    return params;
  }

  @Override public String getDescription() {
    return "method '" + name + "'";
  }

  @Override public boolean isRequired() {
    return required;
  }
}
