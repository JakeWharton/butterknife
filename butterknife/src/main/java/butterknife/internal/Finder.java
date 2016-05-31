package butterknife.internal;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

@SuppressWarnings("UnusedDeclaration") // Used by generated code.
public enum Finder {
  VIEW {
    @Override public View findOptionalView(Object source, int id) {
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
    @Override public View findOptionalView(Object source, int id) {
      return ((Activity) source).findViewById(id);
    }

    @Override public Context getContext(Object source) {
      return (Activity) source;
    }
  },
  DIALOG {
    @Override public View findOptionalView(Object source, int id) {
      return ((Dialog) source).findViewById(id);
    }

    @Override public Context getContext(Object source) {
      return ((Dialog) source).getContext();
    }
  };

  public abstract View findOptionalView(Object source, int id);

  public final <T> T findOptionalViewAsType(Object source, int id, String who, Class<T> cls) {
    View view = findOptionalView(source, id);
    try {
      return cls.cast(view);
    } catch (ClassCastException e) {
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

  public final View findRequiredView(Object source, int id, String who) {
    View view = findOptionalView(source, id);
    if (view != null) {
      return view;
    }
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

  public final <T> T findRequiredViewAsType(Object source, int id, String who, Class<T> cls) {
    View view = findRequiredView(source, id, who);
    try {
      return cls.cast(view);
    } catch (ClassCastException e) {
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
  public final <T> T castView(View view, int id, String who) {
    try {
      return (T) view;
    } catch (ClassCastException e) {
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
  public final <T> T castParam(Object value, String from, int fromPos, String to, int toPos) {
    try {
      return (T) value;
    } catch (ClassCastException e) {
      throw new IllegalStateException("Parameter #"
          + (fromPos + 1)
          + " of method '"
          + from
          + "' was of the wrong type for parameter #"
          + (toPos + 1)
          + " of method '"
          + to
          + "'. See cause for more info.", e);
    }
  }

  protected String getResourceEntryName(Object source, int id) {
    return getContext(source).getResources().getResourceEntryName(id);
  }

  public abstract Context getContext(Object source);
}
