package butterknife;

import android.view.View;
import butterknife.internal.ListenerClass;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Retention(CLASS) @Target(METHOD)
@ListenerClass(View.OnLongClickListener.class)
public @interface OnLongClick {
  int[] value();
}
