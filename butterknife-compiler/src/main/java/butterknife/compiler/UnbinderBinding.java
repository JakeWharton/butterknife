package butterknife.compiler;

import com.squareup.javapoet.ClassName;

final class UnbinderBinding {
  private static final String UNBINDER_SIMPLE_NAME = "Unbinder";

  private final String unbinderFieldName;
  private final ClassName unbinderClassName;

  public UnbinderBinding(String packageName, String enclosingClassName, String fieldName) {
    unbinderClassName = ClassName.get(packageName, enclosingClassName, UNBINDER_SIMPLE_NAME);
    unbinderFieldName = fieldName;
  }

  public String getUnbinderFieldName() {
    return unbinderFieldName;
  }

  public ClassName getUnbinderClassName() {
    return unbinderClassName;
  }
}
