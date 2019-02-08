package com.example.butterknife.functional;

import android.view.View;
import butterknife.BindColor;
import butterknife.ButterKnife;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class BindColorFailureTest {
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindColor(1) String actual;
  }

  @Test public void typeMustBeIntOrColorStateList() {
    Target target = new Target();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindColor field type must be 'int' or 'ColorStateList'. "
              + "(com.example.butterknife.functional.BindColorFailureTest$Target.actual)");
    }
  }
}
