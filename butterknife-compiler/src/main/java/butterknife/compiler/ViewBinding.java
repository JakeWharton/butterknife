package butterknife.compiler;

import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

final class ViewBinding {
  private final Id id;
  private final Map<ListenerClass, Map<ListenerMethod, Set<MethodViewBinding>>> methodBindings;
  private final @Nullable FieldViewBinding fieldBinding;

  ViewBinding(Id id, Map<ListenerClass, Map<ListenerMethod, Set<MethodViewBinding>>> methodBindings,
      @Nullable FieldViewBinding fieldBinding) {
    this.id = id;
    this.methodBindings = methodBindings;
    this.fieldBinding = fieldBinding;
  }

  public Id getId() {
    return id;
  }

  public @Nullable FieldViewBinding getFieldBinding() {
    return fieldBinding;
  }

  public Map<ListenerClass, Map<ListenerMethod, Set<MethodViewBinding>>> getMethodBindings() {
    return methodBindings;
  }

  public List<MemberViewBinding> getRequiredBindings() {
    List<MemberViewBinding> requiredBindings = new ArrayList<>();
    if (fieldBinding != null && fieldBinding.isRequired()) {
      requiredBindings.add(fieldBinding);
    }
    for (Map<ListenerMethod, Set<MethodViewBinding>> methodBinding : methodBindings.values()) {
      for (Set<MethodViewBinding> set : methodBinding.values()) {
        for (MethodViewBinding binding : set) {
          if (binding.isRequired()) {
            requiredBindings.add(binding);
          }
        }
      }
    }
    return requiredBindings;
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

  public static final class Builder {
    private final Id id;

    private final Map<ListenerClass, Map<ListenerMethod, Set<MethodViewBinding>>> methodBindings =
        new LinkedHashMap<>();
    @Nullable FieldViewBinding fieldBinding;

    Builder(Id id) {
      this.id = id;
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

    public ViewBinding build() {
      return new ViewBinding(id, methodBindings, fieldBinding);
    }
  }
}
