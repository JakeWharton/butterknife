package com.example.butterknife.functional;

import android.view.View;
import butterknife.BindInt;
import butterknife.ButterKnife;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class BindIntFailureTest {
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindInt(1) String actual;
  }

  @Test public void typeMustBeInt() {
    Target target = new Target();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindInt field type must be 'int'. "
              + "(com.example.butterknife.functional.BindIntFailureTest$Target.actual)");
    }
  }
}
