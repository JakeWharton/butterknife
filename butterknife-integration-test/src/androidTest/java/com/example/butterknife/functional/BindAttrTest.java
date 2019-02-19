package com.example.butterknife.functional;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.ContextThemeWrapper;
import android.view.View;
import androidx.annotation.ColorInt;
import androidx.test.InstrumentationRegistry;
import butterknife.BindAttr;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.butterknife.test.R;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class BindAttrTest {

  private final Context context = new ContextThemeWrapper(InstrumentationRegistry.getContext(), R.style.Theme_App);
  private final View tree = ViewTree.create(1);

  static class ColorIntTarget {
    @BindAttr(R.attr.colorAccent)
    @ColorInt
    int actual;
  }

  @Test public void asInt() {
    ColorIntTarget target = new ColorIntTarget();
    int expected = Utils.getThemeColor(context, R.attr.colorAccent);

    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertThat(target.actual).isEqualTo(expected);

    unbinder.unbind();
    assertThat(target.actual).isEqualTo(expected);
  }

  static class ColorStateListTarget {
    @BindAttr(R.attr.textColorStateList) ColorStateList actual;
  }

  @Test public void asColorStateList() {
    ColorStateListTarget target = new ColorStateListTarget();
    ColorStateList expected = Utils.getThemeColorStateList(context, R.attr.textColorStateList);

    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertThat(target.actual.toString()).isEqualTo(expected.toString());

    unbinder.unbind();
    assertThat(target.actual.toString()).isEqualTo(expected.toString());
  }
}
