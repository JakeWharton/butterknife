package butterknife.compiler;

import com.squareup.javapoet.TypeName;

public class FieldCollectionBinding {
  enum Kind {
    ARRAY,
    LIST
  }

  private final String name;
  private final Kind kind;
  private final TypeName type;

  public FieldCollectionBinding(String name, Kind kind, TypeName type) {
    this.name = name;
    this.kind = kind;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public Kind getKind() {
    return kind;
  }

  public TypeName getType() {
    return type;
  }

}
