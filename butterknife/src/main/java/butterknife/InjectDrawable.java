package butterknife;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import butterknife.internal.ResourceClass;

@Retention(CLASS) @Target(FIELD)
@ResourceClass(targetType = { "android.graphics.drawable.Drawable" }, getter = "getDrawable")
public @interface InjectDrawable {
  /** Drawable ID to which the field will be bound. */
  int value();
}