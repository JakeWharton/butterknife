package com.example.butterknife.functional;

import android.content.Context;
import android.view.View;
import androidx.test.InstrumentationRegistry;
import butterknife.BindInt;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.butterknife.test.R;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class BindIntTest {
  private final Context context = InstrumentationRegistry.getContext();
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindInt(R.integer.twelve) int actual;
  }

  @Test public void asInt() {
    Target target = new Target();
    int expected = context.getResources().getInteger(R.integer.twelve);

    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertThat(target.actual).isEqualTo(expected);

    unbinder.unbind();
    assertThat(target.actual).isEqualTo(expected);
  }
}
