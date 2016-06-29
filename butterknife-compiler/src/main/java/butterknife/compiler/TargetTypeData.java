package butterknife.compiler;

import javax.lang.model.type.TypeMirror;

public class TargetTypeData {
  private final TypeMirror variableType;
  private final FieldCollectionBinding.Kind kind;
  private final boolean hasError;

  public TargetTypeData(TypeMirror variableType, FieldCollectionBinding.Kind kind,
                        boolean hasError) {
    this.variableType = variableType;
    this.kind = kind;
    this.hasError = hasError;
  }

  public TypeMirror getVariableType() {
    return variableType;
  }

  public FieldCollectionBinding.Kind getKind() {
    return kind;
  }

  public boolean hasError() {
    return hasError;
  }
}
