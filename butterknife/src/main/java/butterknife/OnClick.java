package butterknife;

import butterknife.internal.InjectableListener;
import butterknife.internal.OnClickListenerHandler;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

@InjectableListener(OnClickListenerHandler.class)
@Retention(CLASS) @Target(METHOD)
public @interface OnClick {
  int[] value();
}
