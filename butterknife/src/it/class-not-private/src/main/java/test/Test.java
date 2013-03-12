package test;

import android.app.Activity;
import android.view.View;
import butterknife.InjectView;

public class Test {
  private static class Inner {
    @InjectView(1) View thing;
  }
}
