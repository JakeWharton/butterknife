package com.example.butterknife.unbinder;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.test.InstrumentationRegistry;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class UnbinderTest {
  private final Context context = InstrumentationRegistry.getContext();

  @Test public void verifyContentViewBinding() {
    FrameLayout frameLayout = new FrameLayout(context);
    Button button1 = new Button(context);
    button1.setId(android.R.id.button1);
    frameLayout.addView(button1);
    Button button2 = new Button(context);
    button2.setId(android.R.id.button2);
    frameLayout.addView(button2);
    Button button3 = new Button(context);
    button3.setId(android.R.id.button3);
    frameLayout.addView(button3);
    View content = new View(context);
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
