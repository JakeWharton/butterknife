package com.example.butterknife.unbinder;

import androidx.annotation.ColorInt;
import android.view.View;

import butterknife.BindView;
import butterknife.BindColor;
import butterknife.ButterKnife;

public class H extends G {

  @BindColor(android.R.color.primary_text_dark) @ColorInt int grayColor;
  @BindView(android.R.id.button3) View button3;

  public H(View view) {
    super(view);
    ButterKnife.bind(this, view);
  }
}
