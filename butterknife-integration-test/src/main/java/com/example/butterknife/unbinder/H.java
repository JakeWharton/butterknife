package com.example.butterknife.unbinder;

import android.support.annotation.ColorInt;
import android.view.View;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class H extends G {

  @BindColor(android.R.color.holo_green_dark) @ColorInt int holoGreenDark;
  @BindView(android.R.id.button3) View button3;

  public H(View view) {
    super(view);
    ButterKnife.bind(this, view);
  }
}
