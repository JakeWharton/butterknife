package foo;

import android.app.Activity;
import android.view.View;
import butterknife.InjectView;

public class BaseThing<T> extends Activity {
  @InjectView(1) public View baseThing;

  @Override public View findViewById(int id) {
    if (id == 1) {
      return new View(this);
    }
    return super.findViewById(id);
  }
}
