package butterknife.internal;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

final class ResourceInjection {
  private final int id;
  private final Set<ResourceBinding> resourceBindings = new LinkedHashSet<ResourceBinding>();

  ResourceInjection(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public Set<ResourceBinding> getResourceBindings() {
    return resourceBindings;
  }

  public void addResourceBinding(ResourceBinding resourceBinding) {
    resourceBindings.add(resourceBinding);
  }

  public List<Binding> getRequiredBindings() {
    List<Binding> requiredBindings = new ArrayList<Binding>();

    for (ResourceBinding binding : resourceBindings) {
      if (binding.isRequired()) {
        requiredBindings.add(binding);
      }
    }
    return requiredBindings;
  }
}
