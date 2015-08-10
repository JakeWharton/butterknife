package butterknife.internal;

final class FieldBitmapBinding {
  private final int id;
  private final String name;

  FieldBitmapBinding(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
