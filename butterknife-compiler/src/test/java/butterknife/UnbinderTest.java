package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class UnbinderTest {
  @Test public void bindingUnbinder() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.support.v4.app.Fragment;",
                "import butterknife.ButterKnife;",
                "import butterknife.OnClick;",
                "import butterknife.Unbinder;",
                "public class Test extends Fragment {",
                "  @Unbinder ButterKnife.ViewUnbinder unbinder;",
                "  @OnClick(1) void doStuff() {",
                "  }",
                "}"
            ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.view.View;",
                "import butterknife.ButterKnife;",
                "import butterknife.internal.DebouncingOnClickListener;",
                "import butterknife.internal.Finder;",
                "import butterknife.internal.ViewBinder;",
                "import java.lang.IllegalStateException;",
                "import java.lang.Object;",
                "import java.lang.Override;",
                "import java.lang.SuppressWarnings;",
                "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
                "  @Override public void bind(final Finder finder, final T target, Object source) {",
                "    Unbinder unbinder = createUnbinder(target);",
                "    View view;",
                "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
                "    unbinder.view1 = view;",
                "    view.setOnClickListener(new DebouncingOnClickListener() {",
                "      @Override public void doClick(View p0) {",
                "        target.doStuff();",
                "      }",
                "    });",
                "    target.unbinder = unbinder;",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  protected <U extends Unbinder<T>> U createUnbinder(T target) {",
                "    return (U) new Unbinder(target);",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  protected <U extends Unbinder<T>> U accessUnbinder(T target) {",
                "    return (U) target.unbinder;",
                "  }",
                "  public static class Unbinder<T extends Test> implements ButterKnife.ViewUnbinder<T> {",
                "    private T target;",
                "    View view1;",
                "    protected Unbinder(T target) {",
                "      this.target = target;",
                "    }",
                "    @Override public final void unbind() {",
                "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");",
                "      unbind(target);",
                "      target = null;",
                "    }",
                "    protected void unbind(T target) {",
                "      view1.setOnClickListener(null);",
                "      target.unbinder = null;",
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

  @Test public void failWhenMultipleUnbinders() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.support.v4.app.Fragment;",
                "import butterknife.ButterKnife;",
                "import butterknife.Unbinder;",
                "public class Test extends Fragment {",
                "  @Unbinder ButterKnife.ViewUnbinder unbinder1;",
                "  @Unbinder ButterKnife.ViewUnbinder unbinder2;",
                "}"
            ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "Only one filed should be annotated with @Unbinder. (test.Test.unbinder2)")
        .in(source)
        .onLine(7);
  }

  @Test public void failOnWrongUnbinderType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.support.v4.app.Fragment;",
                "import butterknife.Unbinder;",
                "public class Test extends Fragment {",
                "  @Unbinder Object unbinder;",
                "}"
            ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@Unbinder filed must be of type ButterKnife.ViewUnbinder. (test.Test.unbinder)")
        .in(source)
        .onLine(5);
  }

  @Test public void multipleBindings() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.support.v4.app.Fragment;",
                "import android.view.View;",
                "import butterknife.Bind;",
                "import butterknife.ButterKnife;",
                "import butterknife.OnClick;",
                "import butterknife.OnLongClick;",
                "import butterknife.Unbinder;",
                "public class Test extends Fragment {",
                "  @Unbinder ButterKnife.ViewUnbinder unbinder;",
                "  @Bind(1) View view;",
                "  @Bind(2) View view2;",
                "  @OnClick(1) void doStuff() {",
                "  }",
                "  @OnLongClick(1) boolean doMoreStuff() { return false; }",
                "}"
            ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.view.View;",
                "import butterknife.ButterKnife;",
                "import butterknife.internal.DebouncingOnClickListener;",
                "import butterknife.internal.Finder;",
                "import butterknife.internal.ViewBinder;",
                "import java.lang.IllegalStateException;",
                "import java.lang.Object;",
                "import java.lang.Override;",
                "import java.lang.SuppressWarnings;",
                "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
                "  @Override public void bind(final Finder finder, final T target, Object source) {",
                "    Unbinder unbinder = createUnbinder(target);",
                "    View view;",
                "    view = finder.findRequiredView(source, 1, \"field 'view', method 'doStuff', and method 'doMoreStuff'\");",
                "    target.view = view;",
                "    unbinder.view1 = view;",
                "    view.setOnClickListener(new DebouncingOnClickListener() {",
                "      @Override public void doClick(View p0) {",
                "        target.doStuff();",
                "      }",
                "    });",
                "    view.setOnLongClickListener(new View.OnLongClickListener() {",
                "      @Override public boolean onLongClick(View p0) {",
                "        return target.doMoreStuff();",
                "      }",
                "    });",
                "    view = finder.findRequiredView(source, 2, \"field 'view2'\");",
                "    target.view2 = view;",
                "    target.unbinder = unbinder;",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  protected <U extends Unbinder<T>> U createUnbinder(T target) {",
                "    return (U) new Unbinder(target);",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  protected <U extends Unbinder<T>> U accessUnbinder(T target) {",
                "    return (U) target.unbinder;",
                "  }",
                "  public static class Unbinder<T extends Test> implements ButterKnife.ViewUnbinder<T> {",
                "    private T target;",
                "    View view1;",
                "    protected Unbinder(T target) {",
                "      this.target = target;",
                "    }",
                "    @Override public final void unbind() {",
                "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");",
                "      unbind(target);",
                "      target = null;",
                "    }",
                "    protected void unbind(T target) {",
                "      view1.setOnClickListener(null);",
                "      view1.setOnLongClickListener(null);",
                "      target.view = null;",
                "      target.view2 = null;",
                "      target.unbinder = null;",
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

  @Test public void unbinderRespectsNullable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.support.v4.app.Fragment;",
                "import butterknife.ButterKnife;",
                "import butterknife.OnClick;",
                "import butterknife.Optional;",
                "import butterknife.Unbinder;",
                "public class Test extends Fragment {",
                "  @Unbinder ButterKnife.ViewUnbinder unbinder;",
                "  @Optional @OnClick(1) void doStuff() {",
                "  }",
                "}"
            ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.view.View;",
                "import butterknife.ButterKnife;",
                "import butterknife.internal.DebouncingOnClickListener;",
                "import butterknife.internal.Finder;",
                "import butterknife.internal.ViewBinder;",
                "import java.lang.IllegalStateException;",
                "import java.lang.Object;",
                "import java.lang.Override;",
                "import java.lang.SuppressWarnings;",
                "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
                "  @Override public void bind(final Finder finder, final T target, Object source) {",
                "    Unbinder unbinder = createUnbinder(target);",
                "    View view;",
                "    view = finder.findOptionalView(source, 1, null);",
                "    if (view != null) {",
                "      unbinder.view1 = view;",
                "      view.setOnClickListener(new DebouncingOnClickListener() {",
                "        @Override public void doClick(View p0) {",
                "          target.doStuff();",
                "        }",
                "      });",
                "    }",
                "    target.unbinder = unbinder;",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  protected <U extends Unbinder<T>> U createUnbinder(T target) {",
                "    return (U) new Unbinder(target);",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  protected <U extends Unbinder<T>> U accessUnbinder(T target) {",
                "    return (U) target.unbinder;",
                "  }",
                "  public static class Unbinder<T extends Test> implements ButterKnife.ViewUnbinder<T> {",
                "    private T target;",
                "    View view1;",
                "    protected Unbinder(T target) {",
                "      this.target = target;",
                "    }",
                "    @Override public final void unbind() {",
                "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");",
                "      unbind(target);",
                "      target = null;",
                "    }",
                "    protected void unbind(T target) {",
                "      if (view1 != null) {",
                "        view1.setOnClickListener(null);",
                "      }",
                "      target.unbinder = null;",
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

  @Test public void childBindsSecondUnbinder() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.support.v4.app.Fragment;",
                "import butterknife.ButterKnife;",
                "import butterknife.OnClick;",
                "import butterknife.Unbinder;",
                "public class Test extends Fragment {",
                "  @Unbinder ButterKnife.ViewUnbinder unbinder;",
                "  @OnClick(1) void doStuff1() { }",
                "}",
                "class TestOne extends Test {",
                "  @Unbinder ButterKnife.ViewUnbinder unbinder2;",
                "  @OnClick(1) void doStuff2() { }",
                "}",
                "class TestTwo extends Test {",
                "}"
            ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.view.View;",
                "import butterknife.ButterKnife;",
                "import butterknife.internal.DebouncingOnClickListener;",
                "import butterknife.internal.Finder;",
                "import butterknife.internal.ViewBinder;",
                "import java.lang.IllegalStateException;",
                "import java.lang.Object;",
                "import java.lang.Override;",
                "import java.lang.SuppressWarnings;",
                "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
                "  @Override public void bind(final Finder finder, final T target, Object source) {",
                "    Unbinder unbinder = createUnbinder(target);",
                "    View view;",
                "    view = finder.findRequiredView(source, 1, \"method 'doStuff1'\");",
                "    unbinder.view1 = view;",
                "    view.setOnClickListener(new DebouncingOnClickListener() {",
                "      @Override public void doClick(View p0) {",
                "        target.doStuff1();",
                "      }",
                "    });",
                "    target.unbinder = unbinder;",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  protected <U extends Unbinder<T>> U createUnbinder(T target) {",
                "    return (U) new Unbinder(target);",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  protected <U extends Unbinder<T>> U accessUnbinder(T target) {",
                "    return (U) target.unbinder;",
                "  }",
                "  public static class Unbinder<T extends Test> implements ButterKnife.ViewUnbinder<T> {",
                "    private T target;",
                "    View view1;",
                "    protected Unbinder(T target) {",
                "      this.target = target;",
                "    }",
                "    @Override public final void unbind() {",
                "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");",
                "      unbind(target);",
                "      target = null;",
                "    }",
                "    protected void unbind(T target) {",
                "      view1.setOnClickListener(null);",
                "      target.unbinder = null;",
                "    }",
                "  }",
                "}"
            ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestOne$$ViewBinder",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.view.View;",
                "import butterknife.internal.DebouncingOnClickListener;",
                "import butterknife.internal.Finder;",
                "import java.lang.Object;",
                "import java.lang.Override;",
                "import java.lang.SuppressWarnings;",
                "public class TestOne$$ViewBinder<T extends TestOne> extends Test$$ViewBinder<T> {",
                "  @Override public void bind(final Finder finder, final T target, Object source) {",
                "    super.bind(finder, target, source);",
                "    Unbinder unbinder = super.accessUnbinder(target);",
                "    View view;",
                "    view = finder.findRequiredView(source, 1, \"method 'doStuff2'\");",
                "    unbinder.view1 = view;",
                "    view.setOnClickListener(new DebouncingOnClickListener() {",
                "      @Override public void doClick(View p0) {",
                "        target.doStuff2();",
                "      }",
                "    });",
                "    target.unbinder2 = unbinder;",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  @Override protected <U extends Test$$ViewBinder.Unbinder<T>> U createUnbinder(T target) {",
                "    return (U) new Unbinder(target);",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  @Override protected <U extends Test$$ViewBinder.Unbinder<T>> U accessUnbinder(T target) {",
                "    return (U) target.unbinder2;",
                "  }",
                "  public static class Unbinder<T extends TestOne> extends Test$$ViewBinder.Unbinder<T> {",
                "    View view1;",
                "    protected Unbinder(T target) {",
                "      super(target);",
                "    }",
                "    @Override protected void unbind(T target) {",
                "      super.unbind(target);",
                "      view1.setOnClickListener(null);",
                "      target.unbinder2 = null;",
                "    }",
                "  }",
                "}"
            ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void childUsesOwnUnbinder() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.support.v4.app.Fragment;",
                "import butterknife.ButterKnife;",
                "import butterknife.OnClick;",
                "import butterknife.Unbinder;",
                "public class Test extends Fragment {",
                "  @Unbinder ButterKnife.ViewUnbinder unbinder;",
                "  @OnClick(1) void doStuff1() { }",
                "}",
                "class TestOne extends Test {",
                "  @OnClick(1) void doStuff2() { }",
                "}"
            ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.view.View;",
                "import butterknife.ButterKnife;",
                "import butterknife.internal.DebouncingOnClickListener;",
                "import butterknife.internal.Finder;",
                "import butterknife.internal.ViewBinder;",
                "import java.lang.IllegalStateException;",
                "import java.lang.Object;",
                "import java.lang.Override;",
                "import java.lang.SuppressWarnings;",
                "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
                "  @Override public void bind(final Finder finder, final T target, Object source) {",
                "    Unbinder unbinder = createUnbinder(target);",
                "    View view;",
                "    view = finder.findRequiredView(source, 1, \"method 'doStuff1'\");",
                "    unbinder.view1 = view;",
                "    view.setOnClickListener(new DebouncingOnClickListener() {",
                "      @Override public void doClick(View p0) {",
                "        target.doStuff1();",
                "      }",
                "    });",
                "    target.unbinder = unbinder;",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  protected <U extends Unbinder<T>> U createUnbinder(T target) {",
                "    return (U) new Unbinder(target);",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  protected <U extends Unbinder<T>> U accessUnbinder(T target) {",
                "    return (U) target.unbinder;",
                "  }",
                "  public static class Unbinder<T extends Test> implements ButterKnife.ViewUnbinder<T> {",
                "    private T target;",
                "    View view1;",
                "    protected Unbinder(T target) {",
                "      this.target = target;",
                "    }",
                "    @Override public final void unbind() {",
                "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");",
                "      unbind(target);",
                "      target = null;",
                "    }",
                "    protected void unbind(T target) {",
                "      view1.setOnClickListener(null);",
                "      target.unbinder = null;",
                "    }",
                "  }",
                "}"
            ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestOne$$ViewBinder",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.view.View;",
                "import butterknife.internal.DebouncingOnClickListener;",
                "import butterknife.internal.Finder;",
                "import java.lang.Object;",
                "import java.lang.Override;",
                "import java.lang.SuppressWarnings;",
                "public class TestOne$$ViewBinder<T extends TestOne> extends Test$$ViewBinder<T> {",
                "  @Override public void bind(final Finder finder, final T target, Object source) {",
                "    super.bind(finder, target, source);",
                "    Unbinder unbinder = super.accessUnbinder(target);",
                "    View view;",
                "    view = finder.findRequiredView(source, 1, \"method 'doStuff2'\");",
                "    unbinder.view1 = view;",
                "    view.setOnClickListener(new DebouncingOnClickListener() {",
                "      @Override public void doClick(View p0) {",
                "        target.doStuff2();",
                "      }",
                "    });",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  @Override protected <U extends Test$$ViewBinder.Unbinder<T>> U createUnbinder(T target) {",
                "    return (U) new Unbinder(target);",
                "  }",
                "  public static class Unbinder<T extends TestOne> extends Test$$ViewBinder.Unbinder<T> {",
                "    View view1;",
                "    protected Unbinder(T target) {",
                "      super(target);",
                "    }",
                "    @Override protected void unbind(T target) {",
                "      super.unbind(target);",
                "      view1.setOnClickListener(null);",
                "    }",
                "  }",
                "}"
            ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void childInDifferentPackage() {
    JavaFileObject source1 = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.support.v4.app.Fragment;",
                "import butterknife.ButterKnife;",
                "import butterknife.OnClick;",
                "import butterknife.Unbinder;",
                "public class Test extends Fragment {",
                "  @Unbinder ButterKnife.ViewUnbinder unbinder;",
                "  @OnClick(1) void doStuff1() { }",
                "}"
            ));

    JavaFileObject source2 = JavaFileObjects.forSourceString("test.one.TestOne",
        Joiner.on('\n')
            .join(
                "package test.one;",
                "import test.Test;",
                "import butterknife.OnClick;",
                "class TestOne extends Test {",
                "  @OnClick(2) void doStuff2() { }",
                "}"
            ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.view.View;",
                "import butterknife.ButterKnife;",
                "import butterknife.internal.DebouncingOnClickListener;",
                "import butterknife.internal.Finder;",
                "import butterknife.internal.ViewBinder;",
                "import java.lang.IllegalStateException;",
                "import java.lang.Object;",
                "import java.lang.Override;",
                "import java.lang.SuppressWarnings;",
                "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
                "  @Override public void bind(final Finder finder, final T target, Object source) {",
                "    Unbinder unbinder = createUnbinder(target);",
                "    View view;",
                "    view = finder.findRequiredView(source, 1, \"method 'doStuff1'\");",
                "    unbinder.view1 = view;",
                "    view.setOnClickListener(new DebouncingOnClickListener() {",
                "      @Override public void doClick(View p0) {",
                "        target.doStuff1();",
                "      }",
                "    });",
                "    target.unbinder = unbinder;",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  protected <U extends Unbinder<T>> U createUnbinder(T target) {",
                "    return (U) new Unbinder(target);",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  protected <U extends Unbinder<T>> U accessUnbinder(T target) {",
                "    return (U) target.unbinder;",
                "  }",
                "  public static class Unbinder<T extends Test> implements ButterKnife.ViewUnbinder<T> {",
                "    private T target;",
                "    View view1;",
                "    protected Unbinder(T target) {",
                "      this.target = target;",
                "    }",
                "    @Override public final void unbind() {",
                "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");",
                "      unbind(target);",
                "      target = null;",
                "    }",
                "    protected void unbind(T target) {",
                "      view1.setOnClickListener(null);",
                "      target.unbinder = null;",
                "    }",
                "  }",
                "}"
            ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/one/TestOne$$ViewBinder",
        Joiner.on('\n')
            .join(
                "package test.one;",
                "import android.view.View;",
                "import butterknife.internal.DebouncingOnClickListener;",
                "import butterknife.internal.Finder;",
                "import java.lang.Object;",
                "import java.lang.Override;",
                "import java.lang.SuppressWarnings;",
                "import test.Test$$ViewBinder;",
                "public class TestOne$$ViewBinder<T extends TestOne> extends Test$$ViewBinder<T> {",
                "  @Override public void bind(final Finder finder, final T target, Object source) {",
                "    super.bind(finder, target, source);",
                "    Unbinder unbinder = super.accessUnbinder(target);",
                "    View view;",
                "    view = finder.findRequiredView(source, 2, \"method 'doStuff2'\");",
                "    unbinder.view2 = view;",
                "    view.setOnClickListener(new DebouncingOnClickListener() {",
                "      @Override public void doClick(View p0) {",
                "        target.doStuff2();",
                "      }",
                "    });",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  @Override protected <U extends Test$$ViewBinder.Unbinder<T>> U createUnbinder(T target) {",
                "    return (U) new Unbinder(target);",
                "  }",
                "  public static class Unbinder<T extends TestOne> extends Test$$ViewBinder.Unbinder<T> {",
                "    View view2;",
                "    protected Unbinder(T target) {",
                "      super(target);",
                "    }",
                "    @Override protected void unbind(T target) {",
                "      super.unbind(target);",
                "      view2.setOnClickListener(null);",
                "    }",
                "  }",
                "}"
            ));

    assertAbout(javaSources()).that(asList(source1, source2))
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void unbindingThroughAbstractChild() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.support.v4.app.Fragment;",
                "import butterknife.ButterKnife;",
                "import butterknife.OnClick;",
                "import butterknife.Unbinder;",
                "public class Test extends Fragment {",
                "  @Unbinder ButterKnife.ViewUnbinder unbinder;",
                "  @OnClick(1) void doStuff1() { }",
                "}",
                "class TestOne extends Test {",
                "}",
                "class TestTwo extends TestOne {",
                "  @OnClick(1) void doStuff2() { }",
                "}"
            ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.view.View;",
                "import butterknife.ButterKnife;",
                "import butterknife.internal.DebouncingOnClickListener;",
                "import butterknife.internal.Finder;",
                "import butterknife.internal.ViewBinder;",
                "import java.lang.IllegalStateException;",
                "import java.lang.Object;",
                "import java.lang.Override;",
                "import java.lang.SuppressWarnings;",
                "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
                "  @Override public void bind(final Finder finder, final T target, Object source) {",
                "    Unbinder unbinder = createUnbinder(target);",
                "    View view;",
                "    view = finder.findRequiredView(source, 1, \"method 'doStuff1'\");",
                "    unbinder.view1 = view;",
                "    view.setOnClickListener(new DebouncingOnClickListener() {",
                "      @Override public void doClick(View p0) {",
                "        target.doStuff1();",
                "      }",
                "    });",
                "    target.unbinder = unbinder;",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  protected <U extends Unbinder<T>> U createUnbinder(T target) {",
                "    return (U) new Unbinder(target);",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  protected <U extends Unbinder<T>> U accessUnbinder(T target) {",
                "    return (U) target.unbinder;",
                "  }",
                "  public static class Unbinder<T extends Test> implements ButterKnife.ViewUnbinder<T> {",
                "    private T target;",
                "    View view1;",
                "    protected Unbinder(T target) {",
                "      this.target = target;",
                "    }",
                "    @Override public final void unbind() {",
                "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");",
                "      unbind(target);",
                "      target = null;",
                "    }",
                "    protected void unbind(T target) {",
                "      view1.setOnClickListener(null);",
                "      target.unbinder = null;",
                "    }",
                "  }",
                "}"
            ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestTwo$$ViewBinder",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.view.View;",
                "import butterknife.internal.DebouncingOnClickListener;",
                "import butterknife.internal.Finder;",
                "import java.lang.Object;",
                "import java.lang.Override;",
                "import java.lang.SuppressWarnings;",
                "public class TestTwo$$ViewBinder<T extends TestTwo> extends Test$$ViewBinder<T> {",
                "  @Override public void bind(final Finder finder, final T target, Object source) {",
                "    super.bind(finder, target, source);",
                "    Unbinder unbinder = super.accessUnbinder(target);",
                "    View view;",
                "    view = finder.findRequiredView(source, 1, \"method 'doStuff2'\");",
                "    unbinder.view1 = view;",
                "    view.setOnClickListener(new DebouncingOnClickListener() {",
                "      @Override public void doClick(View p0) {",
                "        target.doStuff2();",
                "      }",
                "    });",
                "  }",
                "  @SuppressWarnings(\"unchecked\")",
                "  @Override protected <U extends Test$$ViewBinder.Unbinder<T>> U createUnbinder(T target) {",
                "    return (U) new Unbinder(target);",
                "  }",
                "  public static class Unbinder<T extends TestTwo> extends Test$$ViewBinder.Unbinder<T> {",
                "    View view1;",
                "    protected Unbinder(T target) {",
                "      super(target);",
                "    }",
                "    @Override protected void unbind(T target) {",
                "      super.unbind(target);",
                "      view1.setOnClickListener(null);",
                "    }",
                "  }",
                "}"
            ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource1, expectedSource2);
  }
}
