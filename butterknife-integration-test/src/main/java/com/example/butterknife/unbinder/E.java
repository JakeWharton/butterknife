package com.example.butterknife.unbinder;

import android.view.View;
import androidx.annotation.ColorInt;
import butterknife.BindColor;
import butterknife.ButterKnife;

public class E extends C {

  @BindColor(android.R.color.background_dark) @ColorInt int backgroundDarkColor;

  public E(View view) {
    super(view);
    ButterKnife.bind(this, view);
  }
}
