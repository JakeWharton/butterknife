package com.example.butterknife.functional;

import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class BindViewTest {
  static class TargetView {
    @BindView(1) View actual;
  }

  @Test public void view() {
    View tree = ViewTree.create(1);
    View expected = tree.findViewById(1);

    TargetView target = new TargetView();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertThat(target.actual).isSameAs(expected);

    unbinder.unbind();
    assertThat(target.actual).isNull();
  }
}
