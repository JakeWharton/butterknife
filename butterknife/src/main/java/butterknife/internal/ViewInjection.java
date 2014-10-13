package butterknife.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ViewInjection {
  private final int id;
  private final Set<ViewBinding> viewBindings = new LinkedHashSet<ViewBinding>();
  private final LinkedHashMap<ListenerClass, Map<ListenerMethod, Set<ListenerBinding>>>
      listenerBindings = new LinkedHashMap<ListenerClass,
      Map<ListenerMethod, Set<ListenerBinding>>>();

  ViewInjection(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public Collection<ViewBinding> getViewBindings() {
    return viewBindings;
  }

  public Map<ListenerClass, Map<ListenerMethod, Set<ListenerBinding>>> getListenerBindings() {
    return listenerBindings;
  }

  public boolean hasListenerBinding(ListenerClass listener, ListenerMethod method) {
    Map<ListenerMethod, Set<ListenerBinding>> methods = listenerBindings.get(listener);
    return methods != null && methods.containsKey(method);
  }

  public void addListenerBinding(ListenerClass listener, ListenerMethod method,
      ListenerBinding binding) {
    Map<ListenerMethod, Set<ListenerBinding>> methods = listenerBindings.get(listener);
    Set<ListenerBinding> set = null;
    if (methods == null) {
      methods = new LinkedHashMap<ListenerMethod, Set<ListenerBinding>>();
      listenerBindings.put(listener, methods);
    } else {
      set = methods.get(method);
    }
    if (set == null) {
      set = new LinkedHashSet<ListenerBinding>();
      methods.put(method, set);
    }
    set.add(binding);
  }

  public void addViewBinding(ViewBinding viewBinding) {
    viewBindings.add(viewBinding);
  }

  public List<Binding> getRequiredBindings() {
    List<Binding> requiredBindings = new ArrayList<Binding>();
    for (ViewBinding viewBinding : viewBindings) {
      if (viewBinding.isRequired()) {
        requiredBindings.add(viewBinding);
      }
    }
    for (Map<ListenerMethod, Set<ListenerBinding>> methodBinding : listenerBindings.values()) {
      for (Set<ListenerBinding> set : methodBinding.values()) {
        for (ListenerBinding binding : set) {
          if (binding.isRequired()) {
            requiredBindings.add(binding);
          }
        }
      }
    }
    return requiredBindings;
  }
}
