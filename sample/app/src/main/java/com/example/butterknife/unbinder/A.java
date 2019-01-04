package com.example.butterknife.unbinder;

import androidx.annotation.ColorInt;
import android.view.View;

import butterknife.BindColor;
import butterknife.ButterKnife;

public class A {

  @BindColor(android.R.color.black) @ColorInt int blackColor;

  public A(View view) {
    ButterKnife.bind(this, view);
  }
}
