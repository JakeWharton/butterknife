package butterknife.internal;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

@SuppressWarnings("UnusedDeclaration") // Used by generated code.
public enum Finder {
  VIEW {
    @Override protected View findView(Object source, int id) {
      return ((View) source).findViewById(id);
    }

    @Override public Context getContext(Object source) {
      return ((View) source).getContext();
    }

    @Override protected String getResourceEntryName(Object source, int id) {
      final View view = (View) source;
      // In edit mode, getResourceEntryName() is unsupported due to use of BridgeResources
      if (view.isInEditMode()) {
        return "<unavailable while editing>";
      }
      return super.getResourceEntryName(source, id);
    }
  },
  ACTIVITY {
    @Override protected View findView(Object source, int id) {
      return ((Activity) source).findViewById(id);
    }

    @Override public Context getContext(Object source) {
      return (Activity) source;
    }
  },
  DIALOG {
    @Override protected View findView(Object source, int id) {
      return ((Dialog) source).findViewById(id);
    }

    @Override public Context getContext(Object source) {
      return ((Dialog) source).getContext();
    }
  };

  public <T> T findRequiredView(Object source, int id, String who) {
    T view = findOptionalView(source, id, who);
    if (view == null) {
      String name = getResourceEntryName(source, id);
      throw new IllegalStateException("Required view '"
          + name
          + "' with ID "
          + id
          + " for "
          + who
          + " was not found. If this view is optional add '@Nullable' (fields) or '@Optional'"
          + " (methods) annotation.");
    }
    return view;
  }

  public <T> T findOptionalView(Object source, int id, String who) {
    View view = findView(source, id);
    return castView(view, id, who);
  }

  @SuppressWarnings("unchecked") // That's the point.
  public <T> T castView(View view, int id, String who) {
    try {
      return (T) view;
    } catch (ClassCastException e) {
      if (who == null) {
        throw new AssertionError();
      }
      String name = getResourceEntryName(view, id);
      throw new IllegalStateException("View '"
          + name
          + "' with ID "
          + id
          + " for "
          + who
          + " was of the wrong type. See cause for more info.", e);
    }
  }

  @SuppressWarnings("unchecked") // That's the point.
  public <T> T castParam(Object value, String from, int fromPosition, String to, int toPosition) {
    try {
      return (T) value;
    } catch (ClassCastException e) {
      throw new IllegalStateException("Parameter #"
          + (fromPosition + 1)
          + " of method '"
          + from
          + "' was of the wrong type for parameter #"
          + (toPosition + 1)
          + " of method '"
          + to
          + "'. See cause for more info.", e);
    }
  }

  protected String getResourceEntryName(Object source, int id) {
    return getContext(source).getResources().getResourceEntryName(id);
  }

  protected abstract View findView(Object source, int id);

  public abstract Context getContext(Object source);
}
