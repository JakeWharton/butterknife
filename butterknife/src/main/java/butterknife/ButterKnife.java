package butterknife;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.util.Property;
import android.view.View;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Field and method binding for Android views. Use this class to simplify finding views and
 * attaching listeners by binding them with annotations.
 * <p>
 * Finding views from your activity is as easy as:
 * <pre><code>
 * public class ExampleActivity extends Activity {
 *   {@literal @}BindView(R.id.title) EditText titleView;
 *   {@literal @}BindView(R.id.subtitle) EditText subtitleView;
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
 * {@literal @}BindView({R.id.first_name, R.id.middle_name, R.id.last_name})
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
 * If a view is optional add a {@code @Nullable} annotation for fields (such as the one in the
 * <a href="http://tools.android.com/tech-docs/support-annotations">support-annotations</a> library)
 * or the {@code @Optional} annotation for methods.
 * <pre><code>
 * {@literal @}Nullable @BindView(R.id.title) TextView subtitleView;
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

  /** An action that can be applied to a list of views. */
  public interface Action<T extends View> {
    /** Apply the action on the {@code view} which is at {@code index} in the list. */
    @UiThread
    void apply(@NonNull T view, int index);
  }

  /** A setter that can apply a value to a list of views. */
  public interface Setter<T extends View, V> {
    /** Set the {@code value} on the {@code view} which is at {@code index} in the list. */
    @UiThread
    void set(@NonNull T view, V value, int index);
  }

  private static final String TAG = "ButterKnife";
  private static boolean debug = false;

  @VisibleForTesting
  static final Map<Class<?>, Constructor<? extends Unbinder>> BINDINGS = new LinkedHashMap<>();

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
    return createBinding(target, sourceView);
  }

  /**
   * BindView annotated fields and methods in the specified {@link View}. The view and its children
   * are used as the view root.
   *
   * @param target Target view for view binding.
   */
  @NonNull @UiThread
  public static Unbinder bind(@NonNull View target) {
    return createBinding(target, target);
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
    return createBinding(target, sourceView);
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
    return createBinding(target, sourceView);
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
    return createBinding(target, source);
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
    return createBinding(target, sourceView);
  }

  private static Unbinder createBinding(@NonNull Object target, @NonNull View source) {
    Class<?> targetClass = target.getClass();
    if (debug) Log.d(TAG, "Looking up binding for " + targetClass.getName());
    Constructor<? extends Unbinder> constructor = findBindingConstructorForClass(targetClass);

    if (constructor == null) {
      return Unbinder.EMPTY;
    }

    //noinspection TryWithIdenticalCatches Resolves to API 19+ only type.
    try {
      return constructor.newInstance(target, source);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to invoke " + constructor, e);
    } catch (InstantiationException e) {
      throw new RuntimeException("Unable to invoke " + constructor, e);
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      if (cause instanceof Error) {
        throw (Error) cause;
      }
      throw new RuntimeException("Unable to create binding instance.", cause);
    }
  }

  @Nullable @CheckResult @UiThread
  private static Constructor<? extends Unbinder> findBindingConstructorForClass(Class<?> cls) {
    Constructor<? extends Unbinder> bindingCtor = BINDINGS.get(cls);
    if (bindingCtor != null) {
      if (debug) Log.d(TAG, "HIT: Cached in binding map.");
      return bindingCtor;
    }
    String clsName = cls.getName();
    if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
      if (debug) Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
      return null;
    }
    try {
      Class<?> bindingClass = cls.getClassLoader().loadClass(clsName + "_ViewBinding");
      //noinspection unchecked
      bindingCtor = (Constructor<? extends Unbinder>) bindingClass.getConstructor(cls, View.class);
      if (debug) Log.d(TAG, "HIT: Loaded binding class and constructor.");
    } catch (ClassNotFoundException e) {
      if (debug) Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
      bindingCtor = findBindingConstructorForClass(cls.getSuperclass());
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
    }
    BINDINGS.put(cls, bindingCtor);
    return bindingCtor;
  }

  /** Apply the specified {@code actions} across the {@code list} of views. */
  @UiThread
  @SafeVarargs public static <T extends View> void apply(@NonNull List<T> list,
      @NonNull Action<? super T>... actions) {
    for (int i = 0, count = list.size(); i < count; i++) {
      for (Action<? super T> action : actions) {
        action.apply(list.get(i), i);
      }
    }
  }

  /** Apply the specified {@code actions} across the {@code array} of views. */
  @UiThread
  @SafeVarargs public static <T extends View> void apply(@NonNull T[] array,
      @NonNull Action<? super T>... actions) {
    for (int i = 0, count = array.length; i < count; i++) {
      for (Action<? super T> action : actions) {
        action.apply(array[i], i);
      }
    }
  }

  /** Apply the specified {@code action} across the {@code list} of views. */
  @UiThread
  public static <T extends View> void apply(@NonNull List<T> list,
      @NonNull Action<? super T> action) {
    for (int i = 0, count = list.size(); i < count; i++) {
      action.apply(list.get(i), i);
    }
  }

  /** Apply the specified {@code action} across the {@code array} of views. */
  @UiThread
  public static <T extends View> void apply(@NonNull T[] array, @NonNull Action<? super T> action) {
    for (int i = 0, count = array.length; i < count; i++) {
      action.apply(array[i], i);
    }
  }

  /** Apply {@code actions} to {@code view}. */
  @UiThread
  @SafeVarargs public static <T extends View> void apply(@NonNull T view,
      @NonNull Action<? super T>... actions) {
    for (Action<? super T> action : actions) {
      action.apply(view, 0);
    }
  }

  /** Apply {@code action} to {@code view}. */
  @UiThread
  public static <T extends View> void apply(@NonNull T view, @NonNull Action<? super T> action) {
    action.apply(view, 0);
  }

  /** Set the {@code value} using the specified {@code setter} across the {@code list} of views. */
  @UiThread
  public static <T extends View, V> void apply(@NonNull List<T> list,
      @NonNull Setter<? super T, V> setter, V value) {
    for (int i = 0, count = list.size(); i < count; i++) {
      setter.set(list.get(i), value, i);
    }
  }

  /** Set the {@code value} using the specified {@code setter} across the {@code array} of views. */
  @UiThread
  public static <T extends View, V> void apply(@NonNull T[] array,
      @NonNull Setter<? super T, V> setter, V value) {
    for (int i = 0, count = array.length; i < count; i++) {
      setter.set(array[i], value, i);
    }
  }

  /** Set {@code value} on {@code view} using {@code setter}. */
  @UiThread
  public static <T extends View, V> void apply(@NonNull T view,
      @NonNull Setter<? super T, V> setter, V value) {
    setter.set(view, value, 0);
  }

  /**
   * Apply the specified {@code value} across the {@code list} of views using the {@code property}.
   */
  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) // http://b.android.com/213630
  @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  @UiThread
  public static <T extends View, V> void apply(@NonNull List<T> list,
      @NonNull Property<? super T, V> setter, V value) {
    //noinspection ForLoopReplaceableByForEach
    for (int i = 0, count = list.size(); i < count; i++) {
      setter.set(list.get(i), value);
    }
  }

  /**
   * Apply the specified {@code value} across the {@code array} of views using the {@code property}.
   */
  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) // http://b.android.com/213630
  @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  @UiThread
  public static <T extends View, V> void apply(@NonNull T[] array,
      @NonNull Property<? super T, V> setter, V value) {
    //noinspection ForLoopReplaceableByForEach
    for (int i = 0, count = array.length; i < count; i++) {
      setter.set(array[i], value);
    }
  }

  /** Apply {@code value} to {@code view} using {@code property}. */
  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) // http://b.android.com/213630
  @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  @UiThread
  public static <T extends View, V> void apply(@NonNull T view,
      @NonNull Property<? super T, V> setter, V value) {
    setter.set(view, value);
  }

  /** @deprecated Compile against API 26 and use {@link View#findViewById(int)}. */
  @SuppressWarnings("UnusedDeclaration") // Public API.
  @CheckResult @Deprecated
  public static <T extends View> T findById(@NonNull View view, @IdRes int id) {
    return view.findViewById(id);
  }

  /** @deprecated Compile against API 26 and use {@link Activity#findViewById(int)}. */
  @SuppressWarnings("UnusedDeclaration") // Public API.
  @CheckResult @Deprecated
  public static <T extends View> T findById(@NonNull Activity activity, @IdRes int id) {
    return activity.findViewById(id);
  }

  /** @deprecated Compile against API 26 and use {@link Dialog#findViewById(int)}. */
  @SuppressWarnings("UnusedDeclaration") // Public API.
  @CheckResult @Deprecated
  public static <T extends View> T findById(@NonNull Dialog dialog, @IdRes int id) {
    return dialog.findViewById(id);
  }
}
