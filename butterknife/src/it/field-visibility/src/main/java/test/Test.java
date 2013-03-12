package test;

import android.app.Activity;
import android.view.View;
import butterknife.InjectView;

public class Test extends Activity {
  @InjectView(1) public View thing1;
  @InjectView(2) protected View thing2;
  @InjectView(3) View thing3;
}
