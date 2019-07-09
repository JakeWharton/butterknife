package com.example.butterknife.functional;

import android.view.View;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import java.util.List;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class BindViewsTest {
  static class TargetViewArray {
    @BindViews({1, 2, 3}) View[] actual;
  }

  @Test public void array() {
    View tree = ViewTree.create(1, 2, 3);
    View expected1 = tree.findViewById(1);
    View expected2 = tree.findViewById(2);
    View expected3 = tree.findViewById(3);

    TargetViewArray target = new TargetViewArray();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertThat(target.actual).asList().containsExactly(expected1, expected2, expected3).inOrder();

    unbinder.unbind();
    assertThat(target.actual).isNull();
  }
  static class TargetViewList {
    @BindViews({1, 2, 3}) List<View> actual;
  }

  @Test public void list() {
    View tree = ViewTree.create(1, 2, 3);
    View expected1 = tree.findViewById(1);
    View expected2 = tree.findViewById(2);
    View expected3 = tree.findViewById(3);

    TargetViewList target = new TargetViewList();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertThat(target.actual).containsExactly(expected1, expected2, expected3).inOrder();

    unbinder.unbind();
    assertThat(target.actual).isNull();
  }
}
