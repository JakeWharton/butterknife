package test;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import java.lang.Override;

public class Target extends Activity {
  @InjectView(1) public View viewOne;
  @InjectView(2) public View viewTwo;
  @InjectView(3) public View viewThree;

  public boolean one;
  public boolean two;
  public boolean three;

  @OnClick(1)
  public void clickOne() {
    one = true;
  }

  @OnClick(2)
  void clickTwo(View view) {
    two = true;
  }

  @OnClick(3)
  protected void clickThree(TextView textView) {
    three = true;
  }

  @Override public View findViewById(int id) {
    return new TextView(this);
  }
}
