package butterknife.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ViewInjection {
  private final int id;
  private final Set<FieldBinding> fieldBindings = new LinkedHashSet<FieldBinding>();
  private final Map<Listener, MethodBinding> methodBindings =
      new LinkedHashMap<Listener, MethodBinding>();

  ViewInjection(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public Collection<FieldBinding> getFieldBindings() {
    return fieldBindings;
  }

  public Map<Listener, MethodBinding> getMethodBindings() {
    return Collections.unmodifiableMap(new LinkedHashMap<Listener, MethodBinding>(methodBindings));
  }

  public List<Binding> getRequiredBindings() {
    List<Binding> requiredBindings = new ArrayList<Binding>();
    for (FieldBinding fieldBinding : fieldBindings) {
      if (fieldBinding.isRequired()) {
        requiredBindings.add(fieldBinding);
      }
    }
    for (MethodBinding methodBinding : methodBindings.values()) {
      if (methodBinding.isRequired()) {
        requiredBindings.add(methodBinding);
      }
    }
    return requiredBindings;
  }

  public void addMethodBinding(Listener listener, MethodBinding methodBinding) {
    MethodBinding existingBinding = methodBindings.get(listener);
    if (existingBinding != null) {
      throw new IllegalStateException("View "
          + id
          + " already has method binding for "
          + listener.getType()
          + " on "
          + existingBinding.getName());
    }
    methodBindings.put(listener, methodBinding);
  }

  public void addFieldBinding(FieldBinding fieldBinding) {
    fieldBindings.add(fieldBinding);
  }
}
