package com.example.butterknife.unbinder;

import androidx.annotation.ColorInt;
import android.view.View;

import butterknife.BindView;
import butterknife.BindColor;
import butterknife.ButterKnife;

public class C extends B {

  @BindColor(android.R.color.transparent) @ColorInt int transparentColor;
  @BindView(android.R.id.button1) View button1;

  public C(View view) {
    super(view);
    ButterKnife.bind(this, view);
  }
}
