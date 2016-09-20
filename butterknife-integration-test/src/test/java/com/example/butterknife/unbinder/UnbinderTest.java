package com.example.butterknife.unbinder;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
public final class UnbinderTest {

  @Test
  public void verifyContentViewBinding() {
    FrameLayout frameLayout = new FrameLayout(RuntimeEnvironment.application);
    Button button1 = new Button(RuntimeEnvironment.application);
    button1.setId(android.R.id.button1);
    frameLayout.addView(button1);
    Button button2 = new Button(RuntimeEnvironment.application);
    button2.setId(android.R.id.button2);
    frameLayout.addView(button2);
    Button button3 = new Button(RuntimeEnvironment.application);
    button3.setId(android.R.id.button3);
    frameLayout.addView(button3);
    View content = new View(RuntimeEnvironment.application);
    content.setId(android.R.id.content);
    frameLayout.addView(content);
    H h = new H(frameLayout);

    Unbinder unbinder = ButterKnife.bind(h, frameLayout);
    verifyHBound(h);
    unbinder.unbind();
    verifyHUnbound(h);
  }

  private void verifyHBound(H h) {
    assertThat(h.button1).isNotNull();
    assertThat(h.button2).isNotNull();
    assertThat(h.button3).isNotNull();
  }

  private void verifyHUnbound(H h) {
    assertThat(h.button1).isNull();
    assertThat(h.button2).isNull();
    assertThat(h.button3).isNull();
  }
}
