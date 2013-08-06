package butterknife;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import butterknife.InjectView;
import butterknife.Optional;
import butterknife.Views;

public class FieldExample extends Activity {
  static boolean HAS_ONE = true;

  @InjectView(1) View thing1;
  @Optional @InjectView(2) View thing2;

  @Override public View findViewById(int id) {
    if (HAS_ONE && id == 1) {
      return new View(this);
    }
    return null;
  }
}
