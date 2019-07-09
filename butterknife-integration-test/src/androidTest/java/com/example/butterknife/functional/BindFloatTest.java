package com.example.butterknife.functional;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import androidx.test.InstrumentationRegistry;
import butterknife.BindFloat;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.butterknife.test.R;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class BindFloatTest {
  private final Context context = InstrumentationRegistry.getContext();
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindFloat(R.dimen.twelve_point_two) float actual;
  }

  @Test public void asFloat() {
    Target target = new Target();
    TypedValue value = new TypedValue();
    context.getResources().getValue(R.dimen.twelve_point_two, value, true);
    float expected = value.getFloat();

    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertThat(target.actual).isEqualTo(expected);

    unbinder.unbind();
    assertThat(target.actual).isEqualTo(expected);
  }
}
