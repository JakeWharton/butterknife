package butterknife.internal;

import static butterknife.internal.ButterKnifeProcessor.VIEW_TYPE;

final class CollectionBinding implements Binding {
  enum Kind {
    ARRAY,
    LIST
  }

  private final String name;
  private final String type;
  private final Kind kind;
  private final boolean required;

  CollectionBinding(String name, String type, Kind kind, boolean required) {
    this.name = name;
    this.type = type;
    this.kind = kind;
    this.required = required;
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

  public boolean isRequired() {
    return required;
  }

  @Override public String getDescription() {
    return "field '" + name + "'";
  }

  public boolean requiresCast() {
    return !VIEW_TYPE.equals(type);
  }
}
