package com.example.butterknife.functional;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import androidx.test.InstrumentationRegistry;

final class ViewTestUtils {
  static View treeWithIds(int... ids) {
    Context context = InstrumentationRegistry.getContext();
    FrameLayout group = new FrameLayout(context);

    class SuperGrossView extends View {
      SuperGrossView(Context context) {
        super(context);
      }

      @Override public boolean post(Runnable action) {
        // Because of DebouncingOnClickListener, we run any posted Runnables synchronously.
        action.run();
        return true;
      }
    }

    for (int id : ids) {
      View view = new SuperGrossView(context);
      view.setId(id);
      group.addView(view);
    }
    return group;
  }

  private ViewTestUtils() {
  }
}
