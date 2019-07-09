package com.example.butterknife.functional;

import android.view.View;
import butterknife.BindBitmap;
import butterknife.ButterKnife;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class BindBitmapFailureTest {
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindBitmap(1) String actual;
  }

  @Test public void typeMustBeBitmap() {
    Target target = new Target();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindBitmap field type must be 'Bitmap'. "
              + "(com.example.butterknife.functional.BindBitmapFailureTest$Target.actual)");
    }
  }
}
