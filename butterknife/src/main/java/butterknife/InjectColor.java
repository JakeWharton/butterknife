package butterknife;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import butterknife.internal.ResourceClass;

@Retention(CLASS) @Target(FIELD)
@ResourceClass(targetType = { "int", "java.lang.Integer" } , getter = "getColor")
public @interface InjectColor {
  /** Color ID to which the field will be bound. */
  int value();
}