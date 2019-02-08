package com.example.butterknife.functional;

import android.view.View;
import butterknife.BindFloat;
import butterknife.ButterKnife;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class BindFloatFailureTest {
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindFloat(1) String actual;
  }

  @Test public void typeMustBeFloat() {
    Target target = new Target();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindFloat field type must be 'float'. "
              + "(com.example.butterknife.functional.BindFloatFailureTest$Target.actual)");
    }
  }
}
