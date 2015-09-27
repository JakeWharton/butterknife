package butterknife.internal;

final class FieldDrawableWithTintBinding {
  private final int id;
  private final String name;
  private final int tintAttributeId;

  FieldDrawableWithTintBinding(int id, String name, int tintAttributeId) {
    this.id = id;
    this.name = name;
    this.tintAttributeId = tintAttributeId;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getTintAttributeId() {
    return tintAttributeId;
  }
}
