package test;

import android.app.Activity;
import android.view.View;
import butterknife.InjectView;

public interface Test {
  @InjectView(1) View thing;
}
