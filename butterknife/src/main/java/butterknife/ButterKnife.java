package butterknife;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.Property;
import android.view.View;
import butterknife.internal.ButterKnifeProcessor;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static butterknife.internal.ButterKnifeProcessor.ANDROID_PREFIX;
import static butterknife.internal.ButterKnifeProcessor.JAVA_PREFIX;

/**
 * Field and method binding for Android views. Use this class to simplify finding views and
 * attaching listeners by binding them with annotations.
 * <p>
 * Finding views from your activity is as easy as:
 * <pre><code>
 * public class ExampleActivity extends Activity {
 *   {@literal @}Bind(R.id.title) EditText titleView;
 *   {@literal @}Bind(R.id.subtitle) EditText subtitleView;
 *
 *   {@literal @}Override protected void onCreate(Bundle savedInstanceState) {
 *     super.onCreate(savedInstanceState);
 *     setContentView(R.layout.example_activity);
 *     ButterKnife.bind(this);
 *   }
 * }
 * </code></pre>
 * Binding can be performed directly on an {@linkplain #bind(Activity) activity}, a
 * {@linkplain #bind(View) view}, or a {@linkplain #bind(Dialog) dialog}. Alternate objects to
 * bind can be specified along with an {@linkplain #bind(Object, Activity) activity},
 * {@linkplain #bind(Object, View) view}, or
 * {@linkplain #bind(Object, android.app.Dialog) dialog}.
 * <p>
 * Group multiple views together into a {@link List} or array.
 * <pre><code>
 * {@literal @}Bind({R.id.first_name, R.id.middle_name, R.id.last_name})
 * List<EditText> nameViews;
 * </code></pre>
 * There are three convenience methods for working with view collections:
 * <ul>
 * <li>{@link #apply(List, Action)} &ndash; Applies an action to each view.</li>
 * <li>{@link #apply(List, Setter, Object)} &ndash; Applies a setter value to each view.</li>
 * <li>{@link #apply(List, Property, Object)} &ndash; Applies a property value to each view.</li>
 * </ul>
 * <p>
 * To bind listeners to your views you can annotate your methods:
 * <pre><code>
 * {@literal @}OnClick(R.id.submit) void onSubmit() {
 *   // React to button click.
 * }
 * </code></pre>
 * Any number of parameters from the listener may be used on the method.
 * <pre><code>
 * {@literal @}OnItemClick(R.id.tweet_list) void onTweetClicked(int position) {
 *   // React to tweet click.
 * }
 * </code></pre>
 * <p>
 * Be default, views are required to be present in the layout for both field and method bindings.
 * If a view is optional add a {@code @Nullable} annotation such as the one in the
 * <a href="http://tools.android.com/tech-docs/support-annotations">support-annotations</a> library.
 * <pre><code>
 * {@literal @}Nullable @Bind(R.id.title) TextView subtitleView;
 * </code></pre>
 * Resources can also be bound to fields to simplify programmatically working with views:
 * <pre><code>
 * {@literal @}BindBool(R.bool.is_tablet) boolean isTablet;
 * {@literal @}BindInt(R.integer.columns) int columns;
 * {@literal @}BindColor(R.color.error_red) int errorRed;
 * </code></pre>
 */
public final class ButterKnife {
  private ButterKnife() {
    throw new AssertionError("No instances.");
  }

  /** DO NOT USE: Exposed for generated code. */
  @SuppressWarnings("UnusedDeclaration") // Used by generated code.
  public enum Finder {
    VIEW {
      @Override protected View findView(Object source, int id) {
        return ((View) source).findViewById(id);
      }

      @Override public Context getContext(Object source) {
        return ((View) source).getContext();
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

    private static <T> T[] filterNull(T[] views) {
      int end = 0;
      for (int i = 0; i < views.length; i++) {
        T view = views[i];
        if (view != null) {
          views[end++] = view;
        }
      }
      return Arrays.copyOfRange(views, 0, end);
    }

    public static <T> T[] arrayOf(T... views) {
      return filterNull(views);
    }

    public static <T> List<T> listOf(T... views) {
      return new ImmutableList<T>(filterNull(views));
    }

    public <T> T findRequiredView(Object source, int id, String who) {
      T view = findOptionalView(source, id, who);
      if (view == null) {
        String name = getContext(source).getResources().getResourceEntryName(id);
        throw new IllegalStateException("Required view '"
            + name
            + "' with ID "
            + id
            + " for "
            + who
            + " was not found. If this view is optional add '@Nullable' annotation.");
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
        String name = view.getResources().getResourceEntryName(id);
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

    protected abstract View findView(Object source, int id);

    public abstract Context getContext(Object source);
  }

  /** DO NOT USE: Exposed for generated code. */
  public interface ViewBinder<T> {
    void bind(Finder finder, T target, Object source);
    void unbind(T target);
  }

  /** An action that can be applied to a list of views. */
  public interface Action<T extends View> {
    /** Apply the action on the {@code view} which is at {@code index} in the list. */
    void apply(T view, int index);
  }

  /** A setter that can apply a value to a list of views. */
  public interface Setter<T extends View, V> {
    /** Set the {@code value} on the {@code view} which is at {@code index} in the list. */
    void set(T view, V value, int index);
  }

  private static final String TAG = "ButterKnife";
  private static boolean debug = false;

  static final Map<Class<?>, ViewBinder<Object>> BINDERS =
      new LinkedHashMap<Class<?>, ViewBinder<Object>>();
  static final ViewBinder<Object> NOP_VIEW_BINDER = new ViewBinder<Object>() {
    @Override public void bind(Finder finder, Object target, Object source) { }
    @Override public void unbind(Object target) { }
  };

  /** Control whether debug logging is enabled. */
  public static void setDebug(boolean debug) {
    ButterKnife.debug = debug;
  }

  /**
   * Bind annotated fields and methods in the specified {@link Activity}. The current content
   * view is used as the view root.
   *
   * @param target Target activity for view binding.
   */
  public static void bind(Activity target) {
    bind(target, target, Finder.ACTIVITY);
  }

  /**
   * Bind annotated fields and methods in the specified {@link View}. The view and its children
   * are used as the view root.
   *
   * @param target Target view for view binding.
   */
  public static void bind(View target) {
    bind(target, target, Finder.VIEW);
  }

  /**
   * Bind annotated fields and methods in the specified {@link Dialog}. The current content
   * view is used as the view root.
   *
   * @param target Target dialog for view binding.
   */
  public static void bind(Dialog target) {
    bind(target, target, Finder.DIALOG);
  }

  /**
   * Bind annotated fields and methods in the specified {@code target} using the {@code source}
   * {@link Activity} as the view root.
   *
   * @param target Target class for view binding.
   * @param source Activity on which IDs will be looked up.
   */
  public static void bind(Object target, Activity source) {
    bind(target, source, Finder.ACTIVITY);
  }

  /**
   * Bind annotated fields and methods in the specified {@code target} using the {@code source}
   * {@link View} as the view root.
   *
   * @param target Target class for view binding.
   * @param source View root on which IDs will be looked up.
   */
  public static void bind(Object target, View source) {
    bind(target, source, Finder.VIEW);
  }

  /**
   * Bind annotated fields and methods in the specified {@code target} using the {@code source}
   * {@link Dialog} as the view root.
   *
   * @param target Target class for view binding.
   * @param source Dialog on which IDs will be looked up.
   */
  public static void bind(Object target, Dialog source) {
    bind(target, source, Finder.DIALOG);
  }

  /**
   * Reset fields annotated with {@link Bind @Bind} to {@code null}.
   * <p>
   * This should only be used in the {@code onDestroyView} method of a fragment.
   *
   * @param target Target class for field unbind.
   */
  public static void unbind(Object target) {
    Class<?> targetClass = target.getClass();
    try {
      if (debug) Log.d(TAG, "Looking up view binder for " + targetClass.getName());
      ViewBinder<Object> viewBinder = findViewBinderForClass(targetClass);
      if (viewBinder != null) {
        viewBinder.unbind(target);
      }
    } catch (Exception e) {
      throw new RuntimeException("Unable to unbind views for " + targetClass.getName(), e);
    }
  }

  static void bind(Object target, Object source, Finder finder) {
    Class<?> targetClass = target.getClass();
    try {
      if (debug) Log.d(TAG, "Looking up view binder for " + targetClass.getName());
      ViewBinder<Object> viewBinder = findViewBinderForClass(targetClass);
      if (viewBinder != null) {
        viewBinder.bind(finder, target, source);
      }
    } catch (Exception e) {
      throw new RuntimeException("Unable to bind views for " + targetClass.getName(), e);
    }
  }

  private static ViewBinder<Object> findViewBinderForClass(Class<?> cls)
      throws IllegalAccessException, InstantiationException {
    ViewBinder<Object> viewBinder = BINDERS.get(cls);
    if (viewBinder != null) {
      if (debug) Log.d(TAG, "HIT: Cached in view binder map.");
      return viewBinder;
    }
    String clsName = cls.getName();
    if (clsName.startsWith(ANDROID_PREFIX) || clsName.startsWith(JAVA_PREFIX)) {
      if (debug) Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
      return NOP_VIEW_BINDER;
    }
    try {
      Class<?> viewBindingClass = Class.forName(clsName + ButterKnifeProcessor.SUFFIX);
      //noinspection unchecked
      viewBinder = (ViewBinder<Object>) viewBindingClass.newInstance();
      if (debug) Log.d(TAG, "HIT: Loaded view binder class.");
    } catch (ClassNotFoundException e) {
      if (debug) Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
      viewBinder = findViewBinderForClass(cls.getSuperclass());
    }
    BINDERS.put(cls, viewBinder);
    return viewBinder;
  }

  /** Apply the specified {@code action} across the {@code list} of views. */
  public static <T extends View> void apply(List<T> list, Action<? super T> action) {
    for (int i = 0, count = list.size(); i < count; i++) {
      action.apply(list.get(i), i);
    }
  }

  /** Set the {@code value} using the specified {@code setter} across the {@code list} of views. */
  public static <T extends View, V> void apply(List<T> list, Setter<? super T, V> setter, V value) {
    for (int i = 0, count = list.size(); i < count; i++) {
      setter.set(list.get(i), value, i);
    }
  }

  /**
   * Apply the specified {@code value} across the {@code list} of views using the {@code property}.
   */
  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  public static <T extends View, V> void apply(List<T> list, Property<? super T, V> setter,
      V value) {
    //noinspection ForLoopReplaceableByForEach
    for (int i = 0, count = list.size(); i < count; i++) {
      setter.set(list.get(i), value);
    }
  }

  /** Simpler version of {@link View#findViewById(int)} which infers the target type. */
  @SuppressWarnings({ "unchecked", "UnusedDeclaration" }) // Checked by runtime cast. Public API.
  public static <T extends View> T findById(View view, int id) {
    return (T) view.findViewById(id);
  }

  /** Simpler version of {@link Activity#findViewById(int)} which infers the target type. */
  @SuppressWarnings({ "unchecked", "UnusedDeclaration" }) // Checked by runtime cast. Public API.
  public static <T extends View> T findById(Activity activity, int id) {
    return (T) activity.findViewById(id);
  }

  /** Simpler version of {@link Dialog#findViewById(int)} which infers the target type. */
  @SuppressWarnings({ "unchecked", "UnusedDeclaration" }) // Checked by runtime cast. Public API.
  public static <T extends View> T findById(Dialog dialog, int id) {
    return (T) dialog.findViewById(id);
  }
}
