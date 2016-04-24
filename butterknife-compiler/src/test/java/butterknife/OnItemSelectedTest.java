package butterknife;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/** This augments {@link OnClickTest} with tests that exercise callbacks with multiple methods. */
public class OnItemSelectedTest {
  @Test public void defaultMethod() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnItemSelected;",
        "public class Test extends Activity {",
        "  @OnItemSelected(1) void doStuff() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.AdapterView;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.ViewBinder;",
            "import java.lang.IllegalStateException;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
            "  @Override",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    InnerUnbinder unbinder = createUnbinder(target);",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    unbinder.view1 = view;",
            "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {",
            "      @Override",
            "      public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {",
            "        target.doStuff();",
            "      }",
            "      @Override",
            "      public void onNothingSelected(AdapterView<?> p0) {",
            "      }",
            "    });",
            "    return unbinder;",
            "  }",
            "  protected InnerUnbinder<T> createUnbinder(T target) {",
            "    return new InnerUnbinder(target);",
            "  }",
            "  protected static class InnerUnbinder<T extends Test> implements Unbinder {",
            "    private T target;",
            "    View view1;",
            "    protected InnerUnbinder(T target) {",
            "      this.target = target;",
            "    }",
            "    @Override",
            "    public final void unbind() {",
            "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");",
            "      unbind(target);",
            "      target = null;",
            "    }",
            "    protected void unbind(T target) {",
            "      ((AdapterView<?>) view1).setOnItemSelectedListener(null);",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void nonDefaultMethod() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnItemSelected;",
        "import static butterknife.OnItemSelected.Callback.NOTHING_SELECTED;",
        "public class Test extends Activity {",
        "  @OnItemSelected(value = 1, callback = NOTHING_SELECTED)",
        "  void doStuff() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.AdapterView;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.ViewBinder;",
            "import java.lang.IllegalStateException;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
            "  @Override",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    InnerUnbinder unbinder = createUnbinder(target);",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    unbinder.view1 = view;",
            "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {",
            "      @Override",
            "      public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {",
            "      }",
            "      @Override",
            "      public void onNothingSelected(AdapterView<?> p0) {",
            "        target.doStuff();",
            "      }",
            "    });",
            "    return unbinder;",
            "  }",
            "  protected InnerUnbinder<T> createUnbinder(T target) {",
            "    return new InnerUnbinder(target);",
            "  }",
            "  protected static class InnerUnbinder<T extends Test> implements Unbinder {",
            "    private T target;",
            "    View view1;",
            "    protected InnerUnbinder(T target) {",
            "      this.target = target;",
            "    }",
            "    @Override",
            "    public final void unbind() {",
            "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");",
            "      unbind(target);",
            "      target = null;",
            "    }",
            "    protected void unbind(T target) {",
            "      ((AdapterView<?>) view1).setOnItemSelectedListener(null);",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void allMethods() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnItemSelected;",
        "import static butterknife.OnItemSelected.Callback.NOTHING_SELECTED;",
        "public class Test extends Activity {",
        "  @OnItemSelected(1)",
        "  void onItemSelected() {}",
        "  @OnItemSelected(value = 1, callback = NOTHING_SELECTED)",
        "  void onNothingSelected() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.AdapterView;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.ViewBinder;",
            "import java.lang.IllegalStateException;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
            "  @Override",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    InnerUnbinder unbinder = createUnbinder(target);",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'onItemSelected' and method 'onNothingSelected'\");",
            "    unbinder.view1 = view;",
            "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {",
            "      @Override",
            "      public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {",
            "        target.onItemSelected();",
            "      }",
            "      @Override",
            "      public void onNothingSelected(AdapterView<?> p0) {",
            "        target.onNothingSelected();",
            "      }",
            "    });",
            "    return unbinder;",
            "  }",
            "  protected InnerUnbinder<T> createUnbinder(T target) {",
            "    return new InnerUnbinder(target);",
            "  }",
            "  protected static class InnerUnbinder<T extends Test> implements Unbinder {",
            "    private T target;",
            "    View view1;",
            "    protected InnerUnbinder(T target) {",
            "      this.target = target;",
            "    }",
            "    @Override",
            "    public final void unbind() {",
            "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");",
            "      unbind(target);",
            "      target = null;",
            "    }",
            "    protected void unbind(T target) {",
            "      ((AdapterView<?>) view1).setOnItemSelectedListener(null);",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void multipleBindingPermutation() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnItemSelected;",
        "import static butterknife.OnItemSelected.Callback.NOTHING_SELECTED;",
        "public class Test extends Activity {",
        "  @OnItemSelected({ 1, 2 })",
        "  void onItemSelected() {}",
        "  @OnItemSelected(value = { 1, 3 }, callback = NOTHING_SELECTED)",
        "  void onNothingSelected() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.AdapterView;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.ViewBinder;",
            "import java.lang.IllegalStateException;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
            "  @Override",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    InnerUnbinder unbinder = createUnbinder(target);",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'onItemSelected' and method 'onNothingSelected'\");",
            "    unbinder.view1 = view;",
            "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {",
            "      @Override",
            "      public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {",
            "        target.onItemSelected();",
            "      }",
            "      @Override",
            "      public void onNothingSelected(AdapterView<?> p0) {",
            "        target.onNothingSelected();",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, 2, \"method 'onItemSelected'\");",
            "    unbinder.view2 = view;",
            "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {",
            "      @Override",
            "      public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {",
            "        target.onItemSelected();",
            "      }",
            "      @Override",
            "      public void onNothingSelected(AdapterView<?> p0) {",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, 3, \"method 'onNothingSelected'\");",
            "    unbinder.view3 = view;",
            "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {",
            "      @Override",
            "      public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {",
            "      }",
            "      @Override",
            "      public void onNothingSelected(AdapterView<?> p0) {",
            "        target.onNothingSelected();",
            "      }",
            "    });",
            "    return unbinder;",
            "  }",
            "  protected InnerUnbinder<T> createUnbinder(T target) {",
            "    return new InnerUnbinder(target);",
            "  }",
            "  protected static class InnerUnbinder<T extends Test> implements Unbinder {",
            "    private T target;",
            "    View view1;",
            "    View view2;",
            "    View view3;",
            "    protected InnerUnbinder(T target) {",
            "      this.target = target;",
            "    }",
            "    @Override",
            "    public final void unbind() {",
            "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");",
            "      unbind(target);",
            "      target = null;",
            "    }",
            "    protected void unbind(T target) {",
            "      ((AdapterView<?>) view1).setOnItemSelectedListener(null);",
            "      ((AdapterView<?>) view2).setOnItemSelectedListener(null);",
            "      ((AdapterView<?>) view3).setOnItemSelectedListener(null);",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
