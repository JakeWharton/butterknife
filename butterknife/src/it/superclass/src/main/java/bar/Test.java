package bar;

import android.app.Activity;
import android.view.View;
import butterknife.InjectView;
import foo.BaseTest;

public class Test extends BaseTest.Thing {
  @InjectView(4) View thing4;
  @InjectView(5) View thing5;
  @InjectView(6) View thing6;
}
