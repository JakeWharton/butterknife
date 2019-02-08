package com.example.butterknife.functional;

import android.view.View;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class BindDrawableFailureTest {
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindDrawable(1) String actual;
  }

  @Test public void typeMustBeDrawable() {
    Target target = new Target();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindDrawable field type must be 'Drawable'. "
              + "(com.example.butterknife.functional.BindDrawableFailureTest$Target.actual)");
    }
  }
}
