package com.example.butterknife.unbinder;

import android.widget.Button;
import android.widget.FrameLayout;

import com.google.common.truth.Truth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import butterknife.ButterKnife;

@RunWith(RobolectricTestRunner.class) //
@Config(manifest = "src/main/AndroidManifest.xml")
public final class UnbinderTest {

  @Test
  public void verifyContentViewBinding() {
    FrameLayout frameLayout = new FrameLayout(Robolectric.application);
    Button button1 = new Button(Robolectric.application);
    button1.setId(android.R.id.button1);
    frameLayout.addView(button1);
    Button button2 = new Button(Robolectric.application);
    button2.setId(android.R.id.button2);
    frameLayout.addView(button2);
    Button button3 = new Button(Robolectric.application);
    button3.setId(android.R.id.button3);
    frameLayout.addView(button3);
    H h = new H(frameLayout);

    ButterKnife.ViewUnbinder unbinder = ButterKnife.bind(h, frameLayout);
    verifyHBound(h);
    unbinder.unbind();
    verifyHUnbound(h);
  }

  private void verifyHBound(H h) {
    Truth.assertThat(h.button1).isNotNull();
    Truth.assertThat(h.button2).isNotNull();
    Truth.assertThat(h.button3).isNotNull();
  }

  private void verifyHUnbound(H h) {
    Truth.assertThat(h.button1).isNull();
    Truth.assertThat(h.button2).isNull();
    Truth.assertThat(h.button3).isNull();
  }

}
