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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static butterknife.internal.ButterKnifeProcessor.ANDROID_PREFIX;
import static butterknife.internal.ButterKnifeProcessor.JAVA_PREFIX;

/**
 * View "injection" utilities. Use this class to simplify finding views and attaching listeners by
 * injecting them.
 * <p>
 * Injecting views from your activity is as easy as:
 * <pre><code>
 * public class ExampleActivity extends Activity {
 *   {@literal @}InjectView(R.id.title) EditText titleView;
 *   {@literal @}InjectView(R.id.subtitle) EditText subtitleView;
 *
 *   {@literal @}Override protected void onCreate(Bundle savedInstanceState) {
 *     super.onCreate(savedInstanceState);
 *     setContentView(R.layout.example_activity);
 *     ButterKnife.inject(this);
 *   }
 * }
 * </code></pre>
 * Injection can be performed directly on an {@linkplain #inject(Activity) activity}, a
 * {@linkplain #inject(View) view}, or a {@linkplain #inject(Dialog) dialog}. Alternate objects to
 * inject can be specified along with an {@linkplain #inject(Object, Activity) activity},
 * {@linkplain #inject(Object, View) view}, or
 * {@linkplain #inject(Object, android.app.Dialog) dialog}.
 * <p>
 * Group multiple views together into a {@link List} or array.
 * <pre><code>
 * {@literal @}InjectViews({R.id.first_name, R.id.middle_name, R.id.last_name})
 * List<EditText> nameViews;
 * </code></pre>
 * There are three convenience methods for working with view collections:
 * <ul>
 * <li>{@link #apply(List, Action)} &ndash; Applies an action to each view.</li>
 * <li>{@link #apply(List, Setter, Object)} &ndash; Applies a setter value to each view.</li>
 * <li>{@link #apply(List, Property, Object)} &ndash; Applies a property value to each view.</li>
 * </ul>
 * <p>
 * To inject listeners to your views you can annotate your methods:
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
 * Be default, views are required to be present in the layout for both field and method injections.
 * If a view is optional add the {@link Optional @Optional} annotation.
 * <pre><code>
 * {@literal @}Optional @InjectView(R.id.title) TextView subtitleView;
 * </code></pre>
 *
 * @see InjectView
 * @see InjectViews
 * @see OnCheckedChanged
 * @see OnClick
 * @see OnEditorAction
 * @see OnFocusChange
 * @see OnItemClick
 * @see OnItemLongClick
 * @see OnItemSelected
 * @see OnLongClick
 * @see OnPageChange
 * @see OnTextChanged
 * @see OnTouch
 */
public final class ButterKnife {
  private ButterKnife() {
    throw new AssertionError("No instances.");
  }

  /** DO NOT USE: Exposed for generated code. */
  @SuppressWarnings("UnusedDeclaration") // Used by generated code.
  public enum Finder {
    VIEW {
      @Override public View findOptionalView(Object source, int id) {
        return ((View) source).findViewById(id);
      }

      @Override protected Context getContext(Object source) {
        return ((View) source).getContext();
      }
    },
    ACTIVITY {
      @Override public View findOptionalView(Object source, int id) {
        return ((Activity) source).findViewById(id);
      }

      @Override protected Context getContext(Object source) {
        return (Activity) source;
      }
    },
    DIALOG {
      @Override public View findOptionalView(Object source, int id) {
        return ((Dialog) source).findViewById(id);
      }

      @Override protected Context getContext(Object source) {
        return ((Dialog) source).getContext();
      }
    };

    public static <T extends View> T[] arrayOf(T... views) {
      return views;
    }

    public static <T extends View> List<T> listOf(T... views) {
      return new ImmutableViewList<T>(views);
    }

    public View findRequiredView(Object source, int id, String who) {
      return findOptionalView(source, id);
    }

    public abstract View findOptionalView(Object source, int id);

    protected abstract Context getContext(Object source);
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

  static final Map<Class<?>, Method> INJECTORS = new LinkedHashMap<Class<?>, Method>();
  static final Map<Class<?>, Method> RESETTERS = new LinkedHashMap<Class<?>, Method>();
  static final Method NO_OP = null;

  /** Control whether debug logging is enabled. */
  public static void setDebug(boolean debug) {
    ButterKnife.debug = debug;
  }

  /**
   * Inject annotated fields and methods in the specified {@link Activity}. The current content
   * view is used as the view root.
   *
   * @param target Target activity for field injection.
   */
  public static void inject(Activity target) {
    inject(target, target.getClass(), target, Finder.ACTIVITY, false);
  }

  /**
   * Inject annotated fields and methods in the specified {@link Activity}. The current content
   * view is used as the view root.
   *
   * @param target Target object for field injection
   * @param targetClass The class-layer limit
   * @param source The activity source to find the views
   */
  public static void inject(Object target, Class<?> targetClass, Activity source) {
    inject(target, targetClass, source, Finder.ACTIVITY, true);
  }

  /**
   * Inject annotated fields and methods in the specified {@link View}. The view and its children
   * are used as the view root.
   *
   * @param target Target view for field injection.
   */
  public static void inject(View target) {
    inject(target, target.getClass(), target, Finder.VIEW, false);
  }

  /**
   * Inject annotated fields and methods in the specified {@link View}. The view and its children
   * are used as the view root.
   *
   * @param target Target object for field injection
   * @param targetClass The class-layer limit
   * @param source The view source to find the views
   */
  public static void inject(Object target, Class<?> targetClass, View source) {
    inject(target, targetClass, source, Finder.VIEW, true);
  }

  /**
   * Inject annotated fields and methods in the specified {@link Dialog}. The current content
   * view is used as the view root.
   *
   * @param target Target dialog for field injection.
   */
  public static void inject(Dialog target) {
    inject(target, target.getClass(), target, Finder.DIALOG, false);
  }

  /**
   * Inject annotated fields and methods in the specified {@link Dialog}. The current content
   * view is used as the view root.
   *
   * @param target Target object for field injection
   * @param targetClass The class-layer limit
   * @param source The dialog source to find the views
   */
  public static void inject(Object target, Class<?> targetClass, Dialog source) {
    inject(target, targetClass, source, Finder.DIALOG, true);
  }

  /**
   * Inject annotated fields and methods in the specified {@code target} using the {@code source}
   * {@link Activity} as the view root.
   *
   * @param target Target class for field injection.
   * @param source Activity on which IDs will be looked up.
   */
  public static void inject(Object target, Activity source) {
    inject(target, target.getClass(), source, Finder.ACTIVITY, false);
  }

  /**
   * Inject annotated fields and methods in the specified {@code target} using the {@code source}
   * {@link View} as the view root.
   *
   * @param target Target class for field injection.
   * @param source View root on which IDs will be looked up.
   */
  public static void inject(Object target, View source) {
    inject(target, target.getClass(), source, Finder.VIEW, false);
  }

  /**
   * Inject annotated fields and methods in the specified {@code target} using the {@code source}
   * {@link Dialog} as the view root.
   *
   * @param target Target class for field injection.
   * @param source Dialog on which IDs will be looked up.
   */
  public static void inject(Object target, Dialog source) {
    inject(target, target.getClass(), source, Finder.DIALOG, true);
  }

  /**
   * Equals to call {@code inject(target, target.getClass())}
   *
   * @param target Target object for field reset.
   */
  public static void reset(Object target) {
    reset(target, target.getClass(), false);
  }

  /**
   * Reset fields annotated with {@link InjectView @InjectView} and
   * {@link InjectViews @InjectViews} to {@code null}.
   * <p>
   * This should only be used in the {@code onDestroyView} method of a fragment.
   *
   * @param target Target object for field reset.
   * @param targetClass The class-layer whose members should be reset.
   * @param exactMatch Should exclude members in super classes of {@code targetClass}
   */
  public static void reset(Object target, Class<?> targetClass, boolean exactMatch) {
    try {
      if (debug) Log.d(TAG, "Looking up view injector for " + targetClass.getName());
      Method reset = findResettersForClass(targetClass);
      if (reset != null) {
        reset.invoke(null, target, exactMatch);
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      Throwable t = e;
      if (t instanceof InvocationTargetException) {
        t = t.getCause();
      }
      throw new RuntimeException("Unable to reset views for " + target, t);
    }
  }

  /**
   * For an object may have multiple class-layers, the {@code targetClass}
   * parameter is used to confine the layer whose direct members or the members
   * declared in its super or super-super class should be injected.
   * @param target The object whose member should be injected
   * @param targetClass The class-layer limit
   * @param source The source to find the views to be injected
   * @param finder An enum instance for {@link Finder}
   * @param exactMatch Should exclude members in super classes of {@code targetClass}
   */
  static void inject(Object target, Class<?> targetClass, Object source, Finder finder, boolean exactMatch) {
    try {
      if (debug) Log.d(TAG, "Looking up view injector for " + targetClass.getName());
      Method inject = findInjectorForClass(targetClass);
      if (inject != null) {
        inject.invoke(null, finder, target, source, exactMatch);
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      Throwable t = e;
      if (t instanceof InvocationTargetException) {
        t = t.getCause();
      }
      throw new RuntimeException("Unable to inject views for " + target, t);
    }
  }

  private static Method findInjectorForClass(Class<?> cls) throws NoSuchMethodException {
    Method inject = INJECTORS.get(cls);
    if (inject != null) {
      if (debug) Log.d(TAG, "HIT: Cached in injector map.");
      return inject;
    }
    String clsName = cls.getName();
    if (clsName.startsWith(ANDROID_PREFIX) || clsName.startsWith(JAVA_PREFIX)) {
      if (debug) Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
      return NO_OP;
    }
    try {
      Class<?> injector = Class.forName(clsName + ButterKnifeProcessor.SUFFIX);
      inject = injector.getMethod("inject", Finder.class, cls, Object.class, Boolean.TYPE);
      if (debug) Log.d(TAG, "HIT: Class loaded injection class.");
    } catch (ClassNotFoundException e) {
      if (debug) Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
      inject = findInjectorForClass(cls.getSuperclass());
    }
    INJECTORS.put(cls, inject);
    return inject;
  }

  private static Method findResettersForClass(Class<?> cls) throws NoSuchMethodException {
    Method inject = RESETTERS.get(cls);
    if (inject != null) {
      if (debug) Log.d(TAG, "HIT: Cached in injector map.");
      return inject;
    }
    String clsName = cls.getName();
    if (clsName.startsWith(ANDROID_PREFIX) || clsName.startsWith(JAVA_PREFIX)) {
      if (debug) Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
      return NO_OP;
    }
    try {
      Class<?> injector = Class.forName(clsName + ButterKnifeProcessor.SUFFIX);
      inject = injector.getMethod("reset", cls, Boolean.TYPE);
      if (debug) Log.d(TAG, "HIT: Class loaded injection class.");
    } catch (ClassNotFoundException e) {
      if (debug) Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
      inject = findResettersForClass(cls.getSuperclass());
    }
    RESETTERS.put(cls, inject);
    return inject;
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
