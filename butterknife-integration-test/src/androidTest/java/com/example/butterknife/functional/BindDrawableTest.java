package com.example.butterknife.functional;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.test.InstrumentationRegistry;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.butterknife.test.R;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class BindDrawableTest {
  private final Context context = InstrumentationRegistry.getContext();
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindDrawable(R.drawable.circle) Drawable actual;
  }

  @Test public void asDrawable() {
    Target target = new Target();
    Drawable expected = context.getResources().getDrawable(R.drawable.circle);

    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertThat(target.actual.getConstantState()).isEqualTo(expected.getConstantState());

    unbinder.unbind();
    assertThat(target.actual.getConstantState()).isEqualTo(expected.getConstantState());
  }
}
