package com.example.butterknife.functional;

import android.view.View;
import butterknife.BindString;
import butterknife.ButterKnife;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class BindStringFailureTest {
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindString(1) boolean actual;
  }

  @Test public void typeMustBeString() {
    Target target = new Target();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindString field type must be 'String'. "
              + "(com.example.butterknife.functional.BindStringFailureTest$Target.actual)");
    }
  }
}
