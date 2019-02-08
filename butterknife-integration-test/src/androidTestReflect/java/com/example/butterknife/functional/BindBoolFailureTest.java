package com.example.butterknife.functional;

import android.view.View;
import butterknife.BindBool;
import butterknife.ButterKnife;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class BindBoolFailureTest {
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindBool(1) String actual;
  }

  @Test public void typeMustBeBool() {
    Target target = new Target();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindBool field type must be 'boolean'. "
              + "(com.example.butterknife.functional.BindBoolFailureTest$Target.actual)");
    }
  }
}
