package com.example.butterknife;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.Extras;
import butterknife.InjectExtra;
import butterknife.InjectView;
import butterknife.Views;

/**
 * User: Nicolas PICON
 * Date: 09/03/13 - 01:21
 */
public class HelloActivity extends Activity {

  public final static String EXTRA_ONE = "EXTRA_ONE";
  public final static String EXTRA_TWO = "EXTRA_TWO";

  @InjectExtra(EXTRA_ONE)
  String firstExtra;
  @InjectExtra(EXTRA_TWO)
  boolean secondExtra;

  @InjectView(R.id.txtHello) TextView txtHello;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.hello_activity);
    Views.inject(this);
    Extras.inject(this);

    StringBuilder sb = new StringBuilder(firstExtra);
    if (secondExtra) {
      sb.append(" sir.");
    }
    else {
      sb.append(" dude!");
    }
    txtHello.setText(sb);
  }
}
