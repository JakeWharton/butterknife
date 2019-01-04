package com.example.butterknife.unbinder;

import android.view.View;
import androidx.annotation.ColorInt;
import butterknife.BindColor;
import butterknife.ButterKnife;

public class D extends C {

  @BindColor(android.R.color.darker_gray) @ColorInt int grayColor;

  public D(View view) {
    super(view);
    ButterKnife.bind(this, view);
  }
}
