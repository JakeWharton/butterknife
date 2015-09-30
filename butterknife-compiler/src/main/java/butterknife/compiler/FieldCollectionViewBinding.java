package butterknife.compiler;

import com.squareup.javapoet.TypeName;

final class FieldCollectionViewBinding implements ViewBinding {
  enum Kind {
    ARRAY,
    LIST
  }

  private final String name;
  private final TypeName type;
  private final Kind kind;
  private final boolean required;

  FieldCollectionViewBinding(String name, TypeName type, Kind kind, boolean required) {
    this.name = name;
    this.type = type;
    this.kind = kind;
    this.required = required;
  }

  public String getName() {
    return name;
  }

  public TypeName getType() {
    return type;
  }

  public Kind getKind() {
    return kind;
  }

  public boolean isRequired() {
    return required;
  }

  @Override public String getDescription() {
    return "field '" + name + "'";
  }
}
