package foo;

import android.app.Activity;
import android.view.View;
import butterknife.InjectView;

public class BaseTest {
  public static class Thing extends Activity {
    @InjectView(1) View thing1;
    @InjectView(2) View thing2;
    @InjectView(3) View thing3;
  }
}
