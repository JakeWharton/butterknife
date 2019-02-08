package com.example.butterknife.functional;

import android.view.View;
import butterknife.BindArray;
import butterknife.ButterKnife;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class BindArrayFailureTest {
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindArray(1) String actual;
  }

  @Test public void typeMustBeSupported() {
    Target target = new Target();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindArray field type must be one of: "
              + "String[], int[], CharSequence[], android.content.res.TypedArray. "
              + "(com.example.butterknife.functional.BindArrayFailureTest$Target.actual)");
    }
  }
}
