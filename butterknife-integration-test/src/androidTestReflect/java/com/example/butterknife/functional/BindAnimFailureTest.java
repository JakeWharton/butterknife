package com.example.butterknife.functional;

import android.view.View;
import butterknife.BindAnim;
import butterknife.ButterKnife;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class BindAnimFailureTest {
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindAnim(1) String actual;
  }

  @Test public void typeMustBeAnimation() {
    Target target = new Target();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindAnim field type must be 'Animation'. "
              + "(com.example.butterknife.functional.BindAnimFailureTest$Target.actual)");
    }
  }
}
