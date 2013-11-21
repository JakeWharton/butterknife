package butterknife;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import butterknife.internal.InjectViewProcessor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * View injection utilities. Use this class to simplify finding views and attaching listeners by
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
 * You can inject an {@link #inject(Activity) activity directly} or a {@link #inject(View) view
 * directly}, or inject an {@link #inject(Object, Activity) activity into another object} or a
 * {@link #inject(Object, View) view into another object}.
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
 * @see OnClick
 * @see OnItemClick
 * @see OnLongClick
 */
public class ButterKnife {
  private ButterKnife() {
    // No instances.
  }

  /**
   * A means of finding a view in either an {@link Activity} or a {@link View}. Exposed for use only
   * by generated code.
   */
  public enum Finder {
    VIEW {
      @Override public View findById(Object source, int id) {
        return ((View) source).findViewById(id);
      }
    },
    ACTIVITY {
      @Override public View findById(Object source, int id) {
        return ((Activity) source).findViewById(id);
      }
    };

    public abstract View findById(Object source, int id);
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
   * Inject fields annotated with {@link InjectView} in the specified {@link Activity}. The current
   * content view is used as the view root.
   *
   * @param target Target activity for field injection.
   * @throws UnableToInjectException if injection could not be performed.
   */
  public static void inject(Activity target) {
    inject(target, target, Finder.ACTIVITY);
  }

  /**
   * Inject fields annotated with {@link InjectView} in the specified {@link View}. The view and
   * its children are used as the view root.
   *
   * @param target Target view for field injection.
   * @throws UnableToInjectException if injection could not be performed.
   */
  public static void inject(View target) {
    inject(target, target, Finder.VIEW);
  }

  /**
   * Inject fields annotated with {@link InjectView} in the specified {@code source} using the
   * {@code target} {@link Activity} as the view root.
   *
   * @param target Target class for field injection.
   * @param source Activity on which IDs will be looked up.
   * @throws UnableToInjectException if injection could not be performed.
   */
  public static void inject(Object target, Activity source) {
    inject(target, source, Finder.ACTIVITY);
  }

  /**
   * Inject fields annotated with {@link InjectView} in the specified {@code source} using the
   * {@code target} {@link View} as the view root.
   *
   * @param target Target class for field injection.
   * @param source View root on which IDs will be looked up.
   * @throws UnableToInjectException if injection could not be performed.
   */
  public static void inject(Object target, View source) {
    inject(target, source, Finder.VIEW);
  }

  /**
   * Reset fields annotated with {@link InjectView} to {@code null}.
   * <p>
   * This should only be used in the {@code onDestroyView} method of a fragment in practice.
   *
   * @param target Target class for field reset.
   * @throws UnableToResetException if views could not be reset.
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
      throw new UnableToResetException("Unable to reset views for " + target, e);
    }
  }

  static void inject(Object target, Object source, Finder finder) {
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
      throw new UnableToInjectException("Unable to inject views for " + target, e);
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
      Class<?> injector = Class.forName(clsName + InjectViewProcessor.SUFFIX);
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
      Class<?> injector = Class.forName(clsName + InjectViewProcessor.SUFFIX);
      inject = injector.getMethod("reset", cls);
      if (debug) Log.d(TAG, "HIT: Class loaded injection class.");
    } catch (ClassNotFoundException e) {
      if (debug) Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
      inject = findResettersForClass(cls.getSuperclass());
    }
    RESETTERS.put(cls, inject);
    return inject;
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

  public static class UnableToInjectException extends RuntimeException {
    UnableToInjectException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class UnableToResetException extends RuntimeException {
    UnableToResetException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
