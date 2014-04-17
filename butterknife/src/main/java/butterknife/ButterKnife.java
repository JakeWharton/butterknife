package butterknife;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.util.Log;
import android.util.Property;
import android.view.View;
import butterknife.internal.ButterKnifeProcessor;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
 * You can inject an {@linkplain #inject(Activity) activity directly} or a{@linkplain #inject(View)
 * view directly}, or inject an {@linkplain #inject(Object, Activity) activity into another object}
 * or a {@linkplain #inject(Object, View) view into another object}.
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
 * @see OnFocusChanged
 * @see OnItemClick
 * @see OnItemLongClick
 * @see OnLongClick
 */
public final class ButterKnife {
  private ButterKnife() {
    throw new AssertionError("No instances.");
  }

  /**
   * Strategy which encapsulates an algorithm of obtaining target {@link View} by id from
   * the given source.
   * <p/>
   * E.g. it might use {@link Activity#findViewById(int)} for {@link Activity} source;
   * {@link View#findViewById(int)} for {@link View} source etc.
   * <p/>
   * <b>Note:</b> there is a bunch of predefined finders used by the library internally.
   * They are used at {@code inject()} methods within the current class
   * (e.g. {@link #inject(View)}, {@link #inject(Activity)}). However, it's always possible
   * to use generic {@link #inject(Object, Object, Finder)} with a custom {@link Finder}
   * implementation.
   */
  public abstract static class Finder {

    public static <T extends View> T[] arrayOf(T... views) {
      return views;
    }

    public static <T extends View> List<T> listOf(T... views) {
      return new ImmutableViewList<T>(views);
    }

    public View findRequiredView(Object source, int id, String who) {
      View view = findOptionalView(source, id);
      if (view == null) {
        throw new IllegalStateException("Required view with id '"
                                        + id
                                        + "' for "
                                        + who
                                        + " was not found. If this view is optional add "
                                        + "'@Optional' annotation.");
      }
      return view;
    }

    public abstract View findOptionalView(Object source, int id);
  }

  private static final Finder VIEW = new Finder() {
    @Override
    public View findOptionalView(Object source, int id) {
      return ((View) source).findViewById(id);
    }
  };

  private static final Finder ACTIVITY = new Finder() {
    @Override
    public View findOptionalView(Object source, int id) {
      return ((Activity) source).findViewById(id);
    }
  };

  private static final Finder DIALOG = new Finder() {
    @Override
    public View findOptionalView(Object source, int id) {
      return ((Dialog) source).findViewById(id);
    }
  };

  /** A simple action that can be applied to a list of views. */
  public interface Action<T extends View> {
    /** Apply the action on the {@code view} which is at {@code index} in the list. */
    void apply(T view, int index);
  }

  /** A simple setter that can be apply a value to a list of views. */
  public interface Setter<T extends View, V> {
    /** Set the {@code value} on the {@code view} which is at {@code index} in the list. */
    void set(T view, V value, int index);
  }

  private static final String  TAG   = "ButterKnife";
  private static       boolean debug = false;

  static final Map<Class<?>, Method> INJECTORS = new LinkedHashMap<Class<?>, Method>();
  static final Map<Class<?>, Method> RESETTERS = new LinkedHashMap<Class<?>, Method>();
  static final Method                NO_OP     = null;

  /** Control whether debug logging is enabled. */
  public static void setDebug(boolean debug) {
    ButterKnife.debug = debug;
  }

  /**
   * Inject fields annotated with {@link InjectView} in the specified {@link Activity}.
   * The current content view is used as the view root.
   *
   * @param target Target activity for field injection.
   */
  public static void inject(Activity target) {
    inject(target, target, ACTIVITY);
  }

  /**
   * Inject fields annotated with {@link InjectView} in the specified {@link View}. The view and
   * its children are used as the view root.
   *
   * @param target Target view for field injection.
   */
  public static void inject(View target) {
    inject(target, target, VIEW);
  }

  /**
   * Inject fields annotated with {@link InjectView} in the specified {@link Dialog}.
   * The current {@link Dialog#getWindow() window}
   * is used as the view root.
   *
   * @param target Target dialog for field injection.
   */
  public static void inject(Dialog target) {
    inject(target, target, DIALOG);
  }

  /**
   * Inject fields annotated with {@link InjectView} in the specified {@code source} using the
   * {@code target} {@link Activity} as the view root.
   *
   * @param target Target class for field injection.
   * @param source Activity on which IDs will be looked up.
   */
  public static void inject(Object target, Activity source) {
    inject(target, source, ACTIVITY);
  }

  /**
   * Inject fields annotated with {@link InjectView} in the specified {@code source} using the
   * {@code target} {@link View} as the view root.
   *
   * @param target Target class for field injection.
   * @param source View root on which IDs will be looked up.
   */
  public static void inject(Object target, View source) {
    inject(target, source, VIEW);
  }

  /**
   * Inject fields annotated with {@link InjectView} in the specified {@code source} using the
   * {@code target} {@link Dialog#getWindow() dialog's window} as the view root.
   *
   * @param target Target class for field injection.
   * @param source Dialog on which IDs will be looked up.
   */
  public static void inject(Object target, Dialog source) {
    inject(target, source, DIALOG);
  }

  /**
   * Reset fields annotated with {@link InjectView} to {@code null}.
   * <p>
   * This should only be used in the {@code onDestroyView} method of a fragment in practice.
   *
   * @param target Target class for field reset.
   */
  public static void reset(Object target) {
    Class<?> targetClass = target.getClass();
    try {
      if (debug) Log.d(TAG, "Looking up view injector for " + targetClass.getName());
      Method reset = findResettersForClass(targetClass);
      if (reset != null) {
        reset.invoke(null, target);
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to reset views for " + target, e);
    }
  }

  public static void inject(Object target, Object source, Finder finder) {
    Class<?> targetClass = target.getClass();
    try {
      if (debug) Log.d(TAG, "Looking up view injector for " + targetClass.getName());
      Method inject = findInjectorForClass(targetClass);
      if (inject != null) {
        inject.invoke(null, finder, target, source);
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to inject views for " + target, e);
    }
  }

  private static Method findInjectorForClass(Class<?> cls) throws NoSuchMethodException {
    Method inject = INJECTORS.get(cls);
    if (inject != null) {
      if (debug) Log.d(TAG, "HIT: Cached in injector map.");
      return inject;
    }
    String clsName = cls.getName();
    if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
      if (debug) Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
      return NO_OP;
    }
    try {
      Class<?> injector = Class.forName(clsName + ButterKnifeProcessor.SUFFIX);
      inject = injector.getMethod("inject", Finder.class, cls, Object.class);
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
    if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
      if (debug) Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
      return NO_OP;
    }
    try {
      Class<?> injector = Class.forName(clsName + ButterKnifeProcessor.SUFFIX);
      inject = injector.getMethod("reset", cls);
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
}
