package butterknife.internal;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

class ViewInjection {
  private final int id;
  private final Set<FieldBinding> fieldBindings = new LinkedHashSet<FieldBinding>();
  private MethodBinding methodBinding;

  ViewInjection(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public Set<FieldBinding> getFieldBindings() {
    return unmodifiableSet(new LinkedHashSet<FieldBinding>(fieldBindings));
  }

  public MethodBinding getMethodBinding() {
    return methodBinding;
  }

  public List<Binding> getRequiredBindings() {
    List<Binding> requiredBindings = new ArrayList<Binding>();
    for (FieldBinding field : fieldBindings) {
      if (field.isRequired()) {
        requiredBindings.add(field);
      }
    }
    if (methodBinding != null && methodBinding.isRequired()) {
      requiredBindings.add(methodBinding);
    }
    return requiredBindings;
  }

  public void addMethodBinding(MethodBinding methodBinding) {
    if (this.methodBinding != null) {
      throw new IllegalStateException(
          "View " + id + " already has method binding: " + this.methodBinding);
    }
    this.methodBinding = methodBinding;
  }

  public void addFieldBinding(FieldBinding fieldBinding) {
    fieldBindings.add(fieldBinding);
  }
}
