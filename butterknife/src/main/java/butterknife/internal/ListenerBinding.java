package butterknife.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class ListenerBinding implements Binding {
  private final String name;
  private final List<Parameter> parameters;
  private final boolean required;

  ListenerBinding(String name, List<Parameter> parameters, boolean required) {
    this.name = name;
    this.parameters = Collections.unmodifiableList(new ArrayList<Parameter>(parameters));
    this.required = required;
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
}
