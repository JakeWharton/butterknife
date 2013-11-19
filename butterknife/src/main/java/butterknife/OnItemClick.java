package butterknife;

import butterknife.internal.InjectableListener;
import butterknife.internal.OnItemClickListenerHandler;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

@InjectableListener(OnItemClickListenerHandler.class)
@Retention(CLASS) @Target(METHOD)
public @interface OnItemClick {
  int[] value();
}
