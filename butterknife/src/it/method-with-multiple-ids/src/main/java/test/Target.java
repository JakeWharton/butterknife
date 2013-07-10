package test;

import android.app.Activity;
import android.view.View;
import butterknife.InjectView;
import butterknife.OnClick;

public class Target extends Activity {
  @InjectView(1) public View viewOne;
  @InjectView(2) public View viewTwo;

  public int count;

  @OnClick({1, 2}) void clickOne() {
    count += 1;
  }

  @Override public View findViewById(int id) {
    return new View(this);
  }
}
