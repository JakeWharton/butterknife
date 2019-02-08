package com.example.butterknife.functional;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import androidx.test.InstrumentationRegistry;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.butterknife.test.R;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class BindColorTest {
  private final Context context = InstrumentationRegistry.getContext();
  private final View tree = ViewTree.create(1);

  static class IntTarget {
    @BindColor(R.color.red) int actual;
  }

  @Test public void asInt() {
    IntTarget target = new IntTarget();
    int expected = context.getResources().getColor(R.color.red);

    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertThat(target.actual).isEqualTo(expected);

    unbinder.unbind();
    assertThat(target.actual).isEqualTo(expected);
  }

  static class ColorStateListTarget {
    @BindColor(R.color.colors) ColorStateList actual;
  }

  @Test public void asColorStateList() {
    ColorStateListTarget target = new ColorStateListTarget();
    ColorStateList expected = context.getResources().getColorStateList(R.color.colors);

    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertThat(target.actual.toString()).isEqualTo(expected.toString());

    unbinder.unbind();
    assertThat(target.actual.toString()).isEqualTo(expected.toString());
  }
}
