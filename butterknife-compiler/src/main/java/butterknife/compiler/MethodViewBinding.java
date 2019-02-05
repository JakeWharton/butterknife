package butterknife.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class MethodViewBinding implements MemberViewBinding {
  private final String name;
  private final List<Parameter> parameters;
  private final boolean required;
  private final boolean hasReturnValue;

  MethodViewBinding(String name, List<Parameter> parameters, boolean required,
      boolean hasReturnValue) {
    this.name = name;
    this.parameters = Collections.unmodifiableList(new ArrayList<>(parameters));
    this.required = required;
    this.hasReturnValue = hasReturnValue;
  }

  public String getName() {
    return name;
  }

  public List<Parameter> getParameters() {
    return parameters;
  }

  @Override public String getDescription() {
    return "method '" + name + "'";
  }

  public boolean isRequired() {
    return required;
  }

  public boolean hasReturnValue() {
    return hasReturnValue;
  }
}
