package test;

import android.app.Activity;
import android.view.View;
import butterknife.InjectView;

public class Target extends Activity {
  @InjectView(1) View thing1;
  @InjectView(1) View thing2;
  @InjectView(1) View thing3;

  int called = 0;

  @Override public View findViewById(int id) {
    called += 1;

    return new View(this);
  }
}
