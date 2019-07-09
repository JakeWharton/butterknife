package com.example.butterknife.functional;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import androidx.core.content.res.ResourcesCompat;
import androidx.test.InstrumentationRegistry;
import androidx.test.filters.SdkSuppress;
import butterknife.BindFont;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.butterknife.test.R;
import org.junit.Test;

import static android.graphics.Typeface.BOLD;
import static com.google.common.truth.Truth.assertThat;

@SdkSuppress(minSdkVersion = 24) // AndroidX problems on earlier versions
public final class BindFontTest {
  private final Context context = InstrumentationRegistry.getContext();
  private final View tree = ViewTree.create(1);

  static class TargetTypeface {
    @BindFont(R.font.inconsolata_regular) Typeface actual;
  }

  @Test public void typeface() {
    TargetTypeface target = new TargetTypeface();
    Typeface expected = ResourcesCompat.getFont(context, R.font.inconsolata_regular);

    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertThat(target.actual).isSameAs(expected);

    unbinder.unbind();
    assertThat(target.actual).isSameAs(expected);
  }

  static class TargetStyle {
    @BindFont(value = R.font.inconsolata_regular, style = BOLD) Typeface actual;
  }

  @Test public void style() {
    TargetStyle target = new TargetStyle();
    Typeface expected =
        Typeface.create(ResourcesCompat.getFont(context, R.font.inconsolata_regular), BOLD);

    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertThat(target.actual).isSameAs(expected);

    unbinder.unbind();
    assertThat(target.actual).isSameAs(expected);
  }
}
