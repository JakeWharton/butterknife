package com.example.butterknife.functional;

import android.view.View;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class BindDimenFailureTest {
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindDimen(1) String actual;
  }

  @Test public void typeMustBeIntOrFloat() {
    Target target = new Target();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindDimen field type must be 'int' or 'float'. "
              + "(com.example.butterknife.functional.BindDimenFailureTest$Target.actual)");
    }
  }
}
