package com.example.butterknife.unbinder;

import androidx.annotation.ColorInt;
import android.view.View;

import butterknife.BindColor;
import butterknife.ButterKnife;

public final class F extends D {

  @BindColor(android.R.color.background_light) @ColorInt int backgroundLightColor;

  public F(View view) {
    super(view);
    ButterKnife.bind(this, view);
  }
}
