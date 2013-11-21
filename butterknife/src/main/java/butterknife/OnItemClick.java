package butterknife;

import android.widget.AdapterView;
import butterknife.internal.ListenerClass;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Retention(CLASS) @Target(METHOD)
@ListenerClass(AdapterView.OnItemClickListener.class)
public @interface OnItemClick {
  int[] value();
}
