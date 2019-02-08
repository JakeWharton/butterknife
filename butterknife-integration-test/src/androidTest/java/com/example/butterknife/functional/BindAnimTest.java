package com.example.butterknife.functional;

import android.view.View;
import android.view.animation.Animation;
import butterknife.BindAnim;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public final class BindAnimTest {
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindAnim(android.R.anim.fade_in) Animation actual;
  }

  @Test public void anim() {
    Target target = new Target();

    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertNotNull(target.actual); // Check more?

    unbinder.unbind();
    assertNotNull(target.actual);
  }
}
