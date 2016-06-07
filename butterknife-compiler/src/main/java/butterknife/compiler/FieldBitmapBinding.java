package butterknife.compiler;

final class FieldBitmapBinding {
  private final Id id;
  private final String name;

  FieldBitmapBinding(Id id, String name) {
    this.id = id;
    this.name = name;
  }

  public Id getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
