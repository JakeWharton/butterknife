package butterknife.compiler;

import com.squareup.javapoet.TypeName;

public class FieldCollectionResourceBinding extends FieldCollectionBinding {

  private final String method;
  private final boolean themeable;

  public FieldCollectionResourceBinding(String name, Kind kind, TypeName type, String method,
                                        boolean themeable) {
    super(name, kind, type);
    this.method = method;
    this.themeable = themeable;
  }

  public String getMethod() {
    return method;
  }

  public boolean isThemeable() {
    return themeable;
  }
}
