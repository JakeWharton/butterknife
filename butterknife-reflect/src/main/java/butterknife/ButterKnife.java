package butterknife;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.View;
import butterknife.internal.Utils;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class ButterKnife {
  private ButterKnife() {
    throw new AssertionError();
  }

  private static final String TAG = "ButterKnife";
  private static boolean debug = false;

  /** Control whether debug logging is enabled. */
  public static void setDebug(boolean debug) {
    ButterKnife.debug = debug;
  }

  /**
   * BindView annotated fields and methods in the specified {@link Activity}. The current content
   * view is used as the view root.
   *
   * @param target Target activity for view binding.
   */
  @NonNull @UiThread
  public static Unbinder bind(@NonNull Activity target) {
    View sourceView = target.getWindow().getDecorView();
    return bind(target, sourceView);
  }

  /**
   * BindView annotated fields and methods in the specified {@link View}. The view and its children
   * are used as the view root.
   *
   * @param target Target view for view binding.
   */
  @NonNull @UiThread
  public static Unbinder bind(@NonNull View target) {
    return bind(target, target);
  }

  /**
   * BindView annotated fields and methods in the specified {@link Dialog}. The current content
   * view is used as the view root.
   *
   * @param target Target dialog for view binding.
   */
  @NonNull @UiThread
  public static Unbinder bind(@NonNull Dialog target) {
    View sourceView = target.getWindow().getDecorView();
    return bind(target, sourceView);
  }

  /**
   * BindView annotated fields and methods in the specified {@code target} using the {@code source}
   * {@link Activity} as the view root.
   *
   * @param target Target class for view binding.
   * @param source Activity on which IDs will be looked up.
   */
  @NonNull @UiThread
  public static Unbinder bind(@NonNull Object target, @NonNull Activity source) {
    View sourceView = source.getWindow().getDecorView();
    return bind(target, sourceView);
  }

  /**
   * BindView annotated fields and methods in the specified {@code target} using the {@code source}
   * {@link Dialog} as the view root.
   *
   * @param target Target class for view binding.
   * @param source Dialog on which IDs will be looked up.
   */
  @NonNull @UiThread
  public static Unbinder bind(@NonNull Object target, @NonNull Dialog source) {
    View sourceView = source.getWindow().getDecorView();
    return bind(target, sourceView);
  }

  /**
   * BindView annotated fields and methods in the specified {@code target} using the {@code source}
   * {@link View} as the view root.
   *
   * @param target Target class for view binding.
   * @param source View root on which IDs will be looked up.
   */
  @NonNull @UiThread
  public static Unbinder bind(@NonNull Object target, @NonNull View source) {
    List<Unbinder> unbinders = new ArrayList<>();
    Class<?> targetClass = target.getClass();
    while (true) {
      String clsName = targetClass.getName();
      if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
        break;
      }

      for (Field field : targetClass.getDeclaredFields()) {
        Unbinder unbinder = parseBindView(target, field, source);
        if (unbinder == null) unbinder = parseBindViews(target, field, source);
        if (unbinder == null) unbinder = parseBindString(target, field, source);

        if (unbinder != null) {
          unbinders.add(unbinder);
        }
      }
      targetClass = targetClass.getSuperclass();
    }

    if (unbinders.isEmpty()) {
      if (debug) Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
      return Unbinder.EMPTY;
    }

    if (debug) Log.d(TAG, "HIT: Reflectively found " + unbinders.size() + " bindings.");
    return new CompositeUnbinder(unbinders);
  }

  private static @Nullable Unbinder parseBindView(Object target, Field field, View source) {
    BindView bindView = field.getAnnotation(BindView.class);
    if (bindView == null) {
      return null;
    }
    // TODO check is instance
    // TODO check visibility
    boolean isRequired = true; // TODO actually figure out

    int id = bindView.value();
    Class<?> viewClass = field.getType();
    String who = "field '" + field.getName() + "'";
    Object view;
    if (isRequired) {
      view = Utils.findRequiredViewAsType(source, id, who, viewClass);
    } else {
      view = Utils.findOptionalViewAsType(source, id, who, viewClass);
    }
    uncheckedSet(field, target, view);

    return new FieldUnbinder(target, field);
  }

  private static @Nullable Unbinder parseBindViews(Object target, Field field, View source) {
    BindViews bindViews = field.getAnnotation(BindViews.class);
    if (bindViews == null) {
      return null;
    }
    // TODO check is instance
    // TODO check visibility
    boolean isRequired = true; // TODO actually figure out

    Class<?> fieldClass = field.getType();
    Class<?> viewClass;
    boolean isArray = fieldClass.isArray();
    if (isArray) {
      viewClass = fieldClass.getComponentType();
    } else if (fieldClass == List.class) {
      Type fieldType = field.getGenericType();
      if (fieldType instanceof ParameterizedType) {
        Type viewType = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
        // TODO real rawType impl!!!!
        viewClass = (Class<?>) viewType;
      } else {
        throw new IllegalStateException(); // TODO
      }
    } else {
      throw new IllegalStateException(); // TODO
    }

    int[] ids = bindViews.value();
    List<Object> views = new ArrayList<>(ids.length);
    String who = "field '" + field.getName() + "'";
    for (int id : ids) {
      Object view;
      if (isRequired) {
        view = Utils.findRequiredViewAsType(source, id, who, viewClass);
      } else {
        view = Utils.findOptionalViewAsType(source, id, who, viewClass);
      }
      if (view != null) {
        views.add(view);
      }
    }

    Object value;
    if (isArray) {
      Object[] viewArray = (Object[]) Array.newInstance(viewClass, views.size());
      value = views.toArray(viewArray);
    } else {
      value = views;
    }

    uncheckedSet(field, target, value);
    return new FieldUnbinder(target, field);
  }

  private static @Nullable Unbinder parseBindString(Object target, Field field, View source) {
    BindString bindString = field.getAnnotation(BindString.class);
    if (bindString == null) {
      return null;
    }
    // TODO check is instance
    // TODO check visibility

    String string = source.getContext().getString(bindString.value());
    uncheckedSet(field, target, string);
    return Unbinder.EMPTY;
  }

  static void uncheckedSet(Field field, Object target, @Nullable Object value) {
    field.setAccessible(true); // TODO move this to a visibility check and only do for package.

    try {
      field.set(target, value);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to assign " + value + " to " + field + " on " + target, e);
    }
  }
}
