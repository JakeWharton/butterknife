package com.example.butterknife.unbinder;

import android.view.View;
import androidx.annotation.ColorInt;
import butterknife.BindColor;
import butterknife.ButterKnife;

public class A {

  @BindColor(android.R.color.black) @ColorInt int blackColor;

  public A(View view) {
    ButterKnife.bind(this, view);
  }
}
