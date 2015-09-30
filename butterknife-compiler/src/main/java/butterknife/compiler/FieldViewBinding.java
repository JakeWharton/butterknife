package butterknife.compiler;

import com.squareup.javapoet.TypeName;

import static butterknife.compiler.ButterKnifeProcessor.VIEW_TYPE;

final class FieldViewBinding implements ViewBinding {
  private final String name;
  private final TypeName type;
  private final boolean required;

  FieldViewBinding(String name, TypeName type, boolean required) {
    this.name = name;
    this.type = type;
    this.required = required;
  }

  public String getName() {
    return name;
  }

  public TypeName getType() {
    return type;
  }

  @Override public String getDescription() {
    return "field '" + name + "'";
  }

  public boolean isRequired() {
    return required;
  }

  public boolean requiresCast() {
    return !VIEW_TYPE.equals(type.toString());
  }
}
