package butterknife.compiler;

final class FieldDrawableBinding {
  private final Id id;
  private final String name;
  private final Id tintAttributeId;

  FieldDrawableBinding(Id id, String name, Id tintAttributeId) {
    this.id = id;
    this.name = name;
    this.tintAttributeId = tintAttributeId;
  }

  public Id getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Id getTintAttributeId() {
    return tintAttributeId;
  }
}
