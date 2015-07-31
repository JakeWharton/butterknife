package butterknife.internal;

import static butterknife.internal.ButterKnifeProcessor.VIEW_TYPE;

final class FieldViewBinding implements ViewBinding {
  private final String name;
  private final String type;
  private final boolean required;

  FieldViewBinding(String name, String type, boolean required) {
    this.name = name;
    this.type = type;
    this.required = required;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  @Override public String getDescription() {
    return "field '" + name + "'";
  }

  public boolean isRequired() {
    return required;
  }

  public boolean requiresCast() {
    return !VIEW_TYPE.equals(type);
  }
}
