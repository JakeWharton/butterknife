package butterknife;

import butterknife.internal.ListenerClass;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Retention(CLASS) @Target(METHOD)
@ListenerClass(
    name = "butterknife.internal.SimpleTextWatcher",
    setter = "addTextChangedListener",
    method = "afterTextChanged",
    owner = "android.widget.TextView"
)
public @interface OnTextChanged {
  int[] value();
}
