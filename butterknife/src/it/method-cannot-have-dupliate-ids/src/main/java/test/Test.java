package test;

import android.app.Activity;
import butterknife.InjectView;
import butterknife.OnClick;
import java.lang.String;

public class Test extends Activity {
  @OnClick({1, 2, 3, 1}) void doStuff() {
  }
}
