package test;

import android.app.Activity;
import android.view.View;
import butterknife.InjectView;
import java.lang.Override;

public class Target extends Activity {
  @InjectView(1) public View thing1;
  @InjectView(2) protected View thing2;
  @InjectView(3) View thing3;

  @Override public View findViewById(int id) {
    return new View(this);
  }
}
