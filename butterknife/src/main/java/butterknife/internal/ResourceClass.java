package butterknife.internal;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME) @Target(ANNOTATION_TYPE)
public @interface ResourceClass {
  /** Fully-qualified class name of the target field type. */
  String[] targetType();

  /** Name of the getter method for the resource. */
  String getter();
}
