package com.example.butterknife.functional;

import android.content.Context;
import android.view.View;
import androidx.test.InstrumentationRegistry;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.butterknife.test.R;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class BindStringTest {
  private final Context context = InstrumentationRegistry.getContext();
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindString(R.string.hey) String actual;
  }

  @Test public void simpleInt() {
    Target target = new Target();
    String expected = context.getString(R.string.hey);

    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertThat(target.actual).isEqualTo(expected);

    unbinder.unbind();
    assertThat(target.actual).isEqualTo(expected);
  }
}
