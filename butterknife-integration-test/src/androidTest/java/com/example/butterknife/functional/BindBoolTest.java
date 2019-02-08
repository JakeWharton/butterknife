package com.example.butterknife.functional;

import android.content.Context;
import android.view.View;
import androidx.test.InstrumentationRegistry;
import butterknife.BindBool;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.butterknife.test.R;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class BindBoolTest {
  private final Context context = InstrumentationRegistry.getContext();
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindBool(R.bool.just_true) boolean actual;
  }

  @Test public void asBoolean() {
    Target target = new Target();
    boolean expected = context.getResources().getBoolean(R.bool.just_true);

    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertThat(target.actual).isEqualTo(expected);

    unbinder.unbind();
    assertThat(target.actual).isEqualTo(expected);
  }
}
