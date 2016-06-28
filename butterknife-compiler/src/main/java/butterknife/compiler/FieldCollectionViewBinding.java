package butterknife.compiler;

import com.squareup.javapoet.TypeName;

final class FieldCollectionViewBinding extends FieldCollectionBinding implements ViewBinding {

  private final boolean required;

  FieldCollectionViewBinding(String name, TypeName type, Kind kind, boolean required) {
    super(name, kind, type);
    this.required = required;
  }

  public boolean isRequired() {
    return required;
  }

  @Override public String getDescription() {
    return "field '" + getName() + "'";
  }
}
