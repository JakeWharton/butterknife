package com.example.butterknife.functional;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import androidx.test.InstrumentationRegistry;
import butterknife.BindBitmap;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.butterknife.test.R;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public final class BindBitmapTest {
  private final Context context = InstrumentationRegistry.getContext();
  private final View tree = ViewTree.create(1);

  static class Target {
    @BindBitmap(R.drawable.pixel) Bitmap actual;
  }

  @Test public void asBitmap() {
    Target target = new Target();
    Bitmap expected = BitmapFactory.decodeResource(context.getResources(), R.drawable.pixel);

    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertTrue(target.actual.sameAs(expected));

    unbinder.unbind();
    assertTrue(target.actual.sameAs(expected));
  }
}
