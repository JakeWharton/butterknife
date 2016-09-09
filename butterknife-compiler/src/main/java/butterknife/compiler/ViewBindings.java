package butterknife.compiler;

import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ViewBindings {
  private final Id id;
  private final Map<ListenerClass, Map<ListenerMethod, Set<MethodViewBinding>>> methodBindings =
      new LinkedHashMap<>();
  private FieldViewBinding fieldBinding;

  ViewBindings(Id id) {
    this.id = id;
  }

  public Id getId() {
    return id;
  }

  public FieldViewBinding getFieldBinding() {
    return fieldBinding;
  }

  public Map<ListenerClass, Map<ListenerMethod, Set<MethodViewBinding>>> getMethodBindings() {
    return methodBindings;
  }

  public boolean hasMethodBinding(ListenerClass listener, ListenerMethod method) {
    Map<ListenerMethod, Set<MethodViewBinding>> methods = methodBindings.get(listener);
    return methods != null && methods.containsKey(method);
  }

  public void addMethodBinding(ListenerClass listener, ListenerMethod method,
      MethodViewBinding binding) {
    Map<ListenerMethod, Set<MethodViewBinding>> methods = methodBindings.get(listener);
    Set<MethodViewBinding> set = null;
    if (methods == null) {
      methods = new LinkedHashMap<>();
      methodBindings.put(listener, methods);
    } else {
      set = methods.get(method);
    }
    if (set == null) {
      set = new LinkedHashSet<>();
      methods.put(method, set);
    }
    set.add(binding);
  }

  public void setFieldBinding(FieldViewBinding fieldBinding) {
    if (this.fieldBinding != null) {
      throw new AssertionError();
    }
    this.fieldBinding = fieldBinding;
  }

  public List<ViewBinding> getRequiredBindings() {
    List<ViewBinding> requiredViewBindings = new ArrayList<>();
    if (fieldBinding != null && fieldBinding.isRequired()) {
      requiredViewBindings.add(fieldBinding);
    }
    for (Map<ListenerMethod, Set<MethodViewBinding>> methodBinding : methodBindings.values()) {
      for (Set<MethodViewBinding> set : methodBinding.values()) {
        for (MethodViewBinding binding : set) {
          if (binding.isRequired()) {
            requiredViewBindings.add(binding);
          }
        }
      }
    }
    return requiredViewBindings;
  }

  public boolean isSingleFieldBinding() {
    return methodBindings.isEmpty() && fieldBinding != null;
  }

  public boolean requiresLocal() {
    if (isBoundToRoot()) {
      return false;
    }
    if (isSingleFieldBinding()) {
      return false;
    }
    return true;
  }

  public boolean isBoundToRoot() {
    return ButterKnifeProcessor.NO_ID.equals(id);
  }
}
