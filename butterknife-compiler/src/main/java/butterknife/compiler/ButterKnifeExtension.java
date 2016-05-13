package butterknife.compiler;

import java.lang.annotation.Annotation;
import java.util.List;

public abstract class ButterKnifeExtension {

  public abstract List<Class<? extends Annotation>> listenerClasses();
}
