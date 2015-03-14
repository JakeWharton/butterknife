package butterknife.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ViewBindings {
  private final int id;
  private final Set<FieldBinding> fieldBindings = new LinkedHashSet<FieldBinding>();
  private final LinkedHashMap<ListenerClass, Map<ListenerMethod, Set<MethodBinding>>>
      methodBindings = new LinkedHashMap<ListenerClass,
      Map<ListenerMethod, Set<MethodBinding>>>();

  ViewBindings(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public Collection<FieldBinding> getFieldBindings() {
    return fieldBindings;
  }

  public Map<ListenerClass, Map<ListenerMethod, Set<MethodBinding>>> getMethodBindings() {
    return methodBindings;
  }

  public boolean hasMethodBinding(ListenerClass listener, ListenerMethod method) {
    Map<ListenerMethod, Set<MethodBinding>> methods = methodBindings.get(listener);
    return methods != null && methods.containsKey(method);
  }

  public void addMethodBinding(ListenerClass listener, ListenerMethod method,
      MethodBinding binding) {
    Map<ListenerMethod, Set<MethodBinding>> methods = methodBindings.get(listener);
    Set<MethodBinding> set = null;
    if (methods == null) {
      methods = new LinkedHashMap<ListenerMethod, Set<MethodBinding>>();
      methodBindings.put(listener, methods);
    } else {
      set = methods.get(method);
    }
    if (set == null) {
      set = new LinkedHashSet<MethodBinding>();
      methods.put(method, set);
    }
    set.add(binding);
  }

  public void addFieldBinding(FieldBinding fieldBinding) {
    fieldBindings.add(fieldBinding);
  }

  public List<Binding> getRequiredBindings() {
    List<Binding> requiredBindings = new ArrayList<Binding>();
    for (FieldBinding fieldBinding : fieldBindings) {
      if (fieldBinding.isRequired()) {
        requiredBindings.add(fieldBinding);
      }
    }
    for (Map<ListenerMethod, Set<MethodBinding>> methodBinding : methodBindings.values()) {
      for (Set<MethodBinding> set : methodBinding.values()) {
        for (MethodBinding binding : set) {
          if (binding.isRequired()) {
            requiredBindings.add(binding);
          }
        }
      }
    }
    return requiredBindings;
  }
}
