package com.example.butterknife.functional;

import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class BindViewFailureTest {
  private final View tree = ViewTree.create(1);

  static class NotView {
    @BindView(1) String actual;
  }

  @Test public void failsIfNotView() {
    NotView target = new NotView();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindView fields must extend from View or be an interface. "
              + "(com.example.butterknife.functional.BindViewFailureTest$NotView.actual)");
    }
  }
}
