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
  private final Map<ListenerClass, Map<ListenerMethod, ListenerBinding>> listenerBindings =
      new LinkedHashMap<ListenerClass, Map<ListenerMethod, ListenerBinding>>();

  ViewInjection(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public Collection<ViewBinding> getViewBindings() {
    return viewBindings;
  }

  public Map<ListenerClass, Map<ListenerMethod, ListenerBinding>> getListenerBindings() {
    return listenerBindings;
  }

  public boolean hasListenerBinding(ListenerClass listener, ListenerMethod method) {
    Map<ListenerMethod, ListenerBinding> methods = listenerBindings.get(listener);
    return methods != null && methods.containsKey(method);
  }

  public void addListenerBinding(ListenerClass listener, ListenerMethod method,
      ListenerBinding binding) {
    Map<ListenerMethod, ListenerBinding> methods = listenerBindings.get(listener);
    if (methods == null) {
      methods = new LinkedHashMap<ListenerMethod, ListenerBinding>();
      listenerBindings.put(listener, methods);
    }
    ListenerBinding existing = methods.get(method);
    if (existing != null) {
      throw new IllegalStateException("View "
          + id
          + " already has listener binding for "
          + listener.type()
          + "."
          + method.name()
          + " on "
          + existing.getDescription());
    }
    methods.put(method, binding);
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
    for (Map<ListenerMethod, ListenerBinding> methodBinding : listenerBindings.values()) {
      for (ListenerBinding binding : methodBinding.values()) {
        if (binding.isRequired()) {
          requiredBindings.add(binding);
        }
      }
    }
    return requiredBindings;
  }
}
