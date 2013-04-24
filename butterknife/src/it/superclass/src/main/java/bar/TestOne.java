package bar;

import android.app.Activity;
import android.view.View;
import butterknife.InjectView;
import foo.BaseThing;

public class TestOne extends BaseThing {
  @InjectView(1) public View thing;
}
