package com.example.butterknife.functional;

import android.graphics.Typeface;
import android.view.View;
import androidx.test.filters.SdkSuppress;
import butterknife.BindFont;
import butterknife.ButterKnife;
import com.example.butterknife.test.R;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class BindFontFailureTest {
  private final View tree = ViewTree.create(1);

  static class TargetType {
    @BindFont(1) String actual;
  }

  @Test public void typeMustBeTypeface() {
    TargetType target = new TargetType();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindFont field type must be 'Typeface'. "
              + "(com.example.butterknife.functional.BindFontFailureTest$TargetType.actual)");
    }
  }

  static class TargetStyle {
    @BindFont(value = R.font.inconsolata_regular, style = 5) Typeface actual;
  }

  @SdkSuppress(minSdkVersion = 24) // AndroidX problems on earlier versions
  @Test public void styleMustBeValid() {
    TargetStyle target = new TargetStyle();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindFont style must be NORMAL, BOLD, ITALIC, or BOLD_ITALIC. "
              + "(com.example.butterknife.functional.BindFontFailureTest$TargetStyle.actual)");
    }
  }
}
