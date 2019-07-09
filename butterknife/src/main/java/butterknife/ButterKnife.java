package butterknife;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.VisibleForTesting;
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
    if (bindingCtor != null || BINDINGS.containsKey(cls)) {
      if (debug) Log.d(TAG, "HIT: Cached in binding map.");
      return bindingCtor;
    }
    String clsName = cls.getName();
    if (clsName.startsWith("android.") || clsName.startsWith("java.")
        || clsName.startsWith("androidx.")) {
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
}
