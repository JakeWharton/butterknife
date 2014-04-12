package butterknife.internal;

final class CollectionBinding {
  enum Kind {
    ARRAY,
    LIST
  }

  private final String name;
  private final String type;
  private final Kind kind;

  CollectionBinding(String name, String type, Kind kind) {
    this.name = name;
    this.type = type;
    this.kind = kind;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public Kind getKind() {
    return kind;
  }
}
