package butterknife.internal;

import java.util.LinkedHashSet;
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
}
