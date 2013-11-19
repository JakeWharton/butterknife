package butterknife.internal;

class MethodBinding implements Binding {
  private final String name;
  private final String annotation;
  private final String[] paramTypes;
  private final boolean required;

  MethodBinding(String name, String annotation, String[] paramTypes, boolean required) {
    this.name = name;
    this.annotation = annotation;
    this.paramTypes = paramTypes;
    this.required = required;
  }

  public String getName() {
    return name;
  }

  public String getAnnotation() {
    return annotation;
  }

  public String[] getParamTypes() {
    return paramTypes;
  }

  @Override public String getDescription() {
    return "method '" + name + "'";
  }

  @Override public boolean isRequired() {
    return required;
  }
}
