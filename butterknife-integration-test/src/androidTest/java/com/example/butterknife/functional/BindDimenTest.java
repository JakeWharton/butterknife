package com.example.butterknife.functional;

import android.content.Context;
import android.view.View;
import androidx.test.InstrumentationRegistry;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.butterknife.test.R;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class BindDimenTest {
  private final Context context = InstrumentationRegistry.getContext();
  private final View tree = ViewTree.create(1);

  static class IntTarget {
    @BindDimen(R.dimen.twelve_point_two_dp) int actual;
  }

  @Test public void asInt() {
    IntTarget target = new IntTarget();
    int expected = context.getResources().getDimensionPixelSize(R.dimen.twelve_point_two_dp);

    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertThat(target.actual).isEqualTo(expected);

    unbinder.unbind();
    assertThat(target.actual).isEqualTo(expected);
  }

  static class FloatTarget {
    @BindDimen(R.dimen.twelve_point_two_dp) float actual;
  }

  @Test public void asFloat() {
    FloatTarget target = new FloatTarget();
    float expected = context.getResources().getDimension(R.dimen.twelve_point_two_dp);

    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertThat(target.actual).isEqualTo(expected);

    unbinder.unbind();
    assertThat(target.actual).isEqualTo(expected);
  }
}
