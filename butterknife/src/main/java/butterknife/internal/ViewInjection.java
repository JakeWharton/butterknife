package butterknife.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ViewInjection {
  private final int id;
  private final Set<FieldBinding> fieldBindings = new LinkedHashSet<FieldBinding>();
  private final Map<String, MethodBinding> methodBindings =
      new LinkedHashMap<String, MethodBinding>();

  ViewInjection(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public Collection<FieldBinding> getFieldBindings() {
    return fieldBindings;
  }

  public Collection<MethodBinding> getMethodBindings() {
    return methodBindings.values();
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

  public void addMethodBinding(MethodBinding methodBinding) {
    String annotation = methodBinding.getAnnotation();
    MethodBinding existingBinding = methodBindings.get(annotation);
    if (existingBinding != null) {
      throw new IllegalStateException("View "
          + id
          + " already has method binding for "
          + annotation
          + " on "
          + existingBinding.getName());
    }
    methodBindings.put(annotation, methodBinding);
  }

  public void addFieldBinding(FieldBinding fieldBinding) {
    fieldBindings.add(fieldBinding);
  }
}
