package com.example.butterknife.functional;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.test.InstrumentationRegistry;
import java.lang.reflect.InvocationTargetException;

final class ViewTree {
  static View create(int... ids) {
    return create(View.class, ids);
  }

  static View create(Class<? extends View> cls, int... ids) {
    Context context = InstrumentationRegistry.getContext();
    ViewGroup group = new FrameLayout(context);
    for (int id : ids) {
      View view;
      if (cls == View.class) {
        view = new NoPostView(context);
      } else {
        try {
          view = cls.getConstructor(Context.class).newInstance(context);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException e) {
          throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
          Throwable cause = e.getCause();
          if (cause instanceof RuntimeException) throw (RuntimeException) cause;
          if (cause instanceof Error) throw (Error) cause;
          throw new RuntimeException(cause);
        }
      }

      view.setId(id);
      group.addView(view);
    }
    return group;
  }

  private static final class NoPostView extends View {
    NoPostView(Context context) {
      super(context);
    }

    @Override public boolean post(Runnable action) {
      // Because of DebouncingOnClickListener, we run any posted Runnables synchronously.
      action.run();
      return true;
    }
  }
}
