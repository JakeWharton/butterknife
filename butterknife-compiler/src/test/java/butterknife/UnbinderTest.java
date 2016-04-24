package butterknife;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

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
                "  @OnClick(1) void doStuff() {",
                "  }",
                "}"
            ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.DebouncingOnClickListener;",
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
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override",
            "      public void doClick(View p0) {",
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
            "      view1.setOnClickListener(null);",
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

  @Test public void multipleBindings() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n')
            .join(
                "package test;",
                "import android.support.v4.app.Fragment;",
                "import android.view.View;",
                "import butterknife.BindView;",
                "import butterknife.ButterKnife;",
                "import butterknife.OnClick;",
                "import butterknife.OnLongClick;",
                "import butterknife.Unbinder;",
                "public class Test extends Fragment {",
                "  @BindView(1) View view;",
                "  @BindView(2) View view2;",
                "  @OnClick(1) void doStuff() {",
                "  }",
                "  @OnLongClick(1) boolean doMoreStuff() { return false; }",
                "}"
            ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.DebouncingOnClickListener;",
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
            "    view = finder.findRequiredView(source, 1, \"field 'view', method 'doStuff', and method 'doMoreStuff'\");",
            "    target.view = view;",
            "    unbinder.view1 = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override",
            "      public void doClick(View p0) {",
            "        target.doStuff();",
            "      }",
            "    });",
            "    view.setOnLongClickListener(new View.OnLongClickListener() {",
            "      @Override",
            "      public boolean onLongClick(View p0) {",
            "        return target.doMoreStuff();",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, 2, \"field 'view2'\");",
            "    target.view2 = view;",
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
            "      view1.setOnClickListener(null);",
            "      view1.setOnLongClickListener(null);",
            "      target.view = null;",
            "      target.view2 = null;",
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
                "  @Optional @OnClick(1) void doStuff() {",
                "  }",
                "}"
            ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.DebouncingOnClickListener;",
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
            "    view = finder.findOptionalView(source, 1, null);",
            "    if (view != null) {",
            "      unbinder.view1 = view;",
            "      view.setOnClickListener(new DebouncingOnClickListener() {",
            "        @Override",
            "        public void doClick(View p0) {",
            "          target.doStuff();",
            "        }",
            "      });",
            "    }",
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
            "      if (view1 != null) {",
            "        view1.setOnClickListener(null);",
            "      }",
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
                "  @OnClick(1) void doStuff1() { }",
                "}",
                "class TestOne extends Test {",
                "  @OnClick(1) void doStuff2() { }",
                "}",
                "class TestTwo extends Test {",
                "}"
            ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.DebouncingOnClickListener;",
            "import butterknife.internal.Finder;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class TestOne$$ViewBinder<T extends TestOne> extends Test$$ViewBinder<T> {",
            "  @Override",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    InnerUnbinder unbinder = (InnerUnbinder) super.bind(finder, target, source);",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff2'\");",
            "    unbinder.view1 = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override",
            "      public void doClick(View p0) {",
            "        target.doStuff2();",
            "      }",
            "    });",
            "    return unbinder;",
            "  }",
            "  @Override",
            "  protected InnerUnbinder<T> createUnbinder(T target) {",
            "    return new InnerUnbinder(target);",
            "  }",
            "  protected static class InnerUnbinder<T extends TestOne> extends Test$$ViewBinder.InnerUnbinder<T> {",
            "    View view1;",
            "    protected InnerUnbinder(T target) {",
            "      super(target);",
            "    }",
            "    @Override",
            "    protected void unbind(T target) {",
            "      super.unbind(target);",
            "      view1.setOnClickListener(null);",
            "    }",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestOne$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.DebouncingOnClickListener;",
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
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff1'\");",
            "    unbinder.view1 = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override",
            "      public void doClick(View p0) {",
            "        target.doStuff1();",
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
                "  @OnClick(1) void doStuff1() { }",
                "}",
                "class TestOne extends Test {",
                "  @OnClick(1) void doStuff2() { }",
                "}"
            ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.DebouncingOnClickListener;",
            "import butterknife.internal.Finder;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class TestOne$$ViewBinder<T extends TestOne> extends Test$$ViewBinder<T> {",
            "  @Override",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    InnerUnbinder unbinder = (InnerUnbinder) super.bind(finder, target, source);",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff2'\");",
            "    unbinder.view1 = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override",
            "      public void doClick(View p0) {",
            "        target.doStuff2();",
            "      }",
            "    });",
            "    return unbinder;",
            "  }",
            "  @Override",
            "  protected InnerUnbinder<T> createUnbinder(T target) {",
            "    return new InnerUnbinder(target);",
            "  }",
            "  protected static class InnerUnbinder<T extends TestOne> extends Test$$ViewBinder.InnerUnbinder<T> {",
            "    View view1;",
            "    protected InnerUnbinder(T target) {",
            "      super(target);",
            "    }",
            "    @Override",
            "    protected void unbind(T target) {",
            "      super.unbind(target);",
            "      view1.setOnClickListener(null);",
            "    }",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestOne$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.DebouncingOnClickListener;",
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
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff1'\");",
            "    unbinder.view1 = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override",
            "      public void doClick(View p0) {",
            "        target.doStuff1();",
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
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.DebouncingOnClickListener;",
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
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff1'\");",
            "    unbinder.view1 = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override",
            "      public void doClick(View p0) {",
            "        target.doStuff1();",
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
            "      view1.setOnClickListener(null);",
            "    }",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/one/TestOne$$ViewBinder",
        Joiner.on('\n').join(
            "package test.one;",
            "import android.view.View;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.DebouncingOnClickListener;",
            "import butterknife.internal.Finder;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "import test.Test$$ViewBinder;",
            "public class TestOne$$ViewBinder<T extends TestOne> extends Test$$ViewBinder<T> {",
            "  @Override",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    InnerUnbinder unbinder = (InnerUnbinder) super.bind(finder, target, source);",
            "    View view;",
            "    view = finder.findRequiredView(source, 2, \"method 'doStuff2'\");",
            "    unbinder.view2 = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override",
            "      public void doClick(View p0) {",
            "        target.doStuff2();",
            "      }",
            "    });",
            "    return unbinder;",
            "  }",
            "  @Override",
            "  protected InnerUnbinder<T> createUnbinder(T target) {",
            "    return new InnerUnbinder(target);",
            "  }",
            "  protected static class InnerUnbinder<T extends TestOne> extends Test$$ViewBinder.InnerUnbinder<T> {",
            "    View view2;",
            "    protected InnerUnbinder(T target) {",
            "      super(target);",
            "    }",
            "    @Override",
            "    protected void unbind(T target) {",
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
                "  @OnClick(1) void doStuff1() { }",
                "}",
                "class TestOne extends Test {",
                "}",
                "class TestTwo extends TestOne {",
                "  @OnClick(1) void doStuff2() { }",
                "}"
            ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.DebouncingOnClickListener;",
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
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff1'\");",
            "    unbinder.view1 = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override",
            "      public void doClick(View p0) {",
            "        target.doStuff1();",
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
            "      view1.setOnClickListener(null);",
            "    }",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestTwo$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.DebouncingOnClickListener;",
            "import butterknife.internal.Finder;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class TestTwo$$ViewBinder<T extends TestTwo> extends Test$$ViewBinder<T> {",
            "  @Override",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    InnerUnbinder unbinder = (InnerUnbinder) super.bind(finder, target, source);",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff2'\");",
            "    unbinder.view1 = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override",
            "      public void doClick(View p0) {",
            "        target.doStuff2();",
            "      }",
            "    });",
            "    return unbinder;",
            "  }",
            "  @Override",
            "  protected InnerUnbinder<T> createUnbinder(T target) {",
            "    return new InnerUnbinder(target);",
            "  }",
            "  protected static class InnerUnbinder<T extends TestTwo> extends Test$$ViewBinder.InnerUnbinder<T> {",
            "    View view1;",
            "    protected InnerUnbinder(T target) {",
            "      super(target);",
            "    }",
            "    @Override",
            "    protected void unbind(T target) {",
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

  @Test public void fullIntegration() {
    JavaFileObject sourceA = JavaFileObjects.forSourceString("test.A", Joiner.on('\n').join(
        "package test;",
        "import android.support.annotation.ColorInt;",
        "import android.view.View;",
        "import butterknife.BindColor;",
        "import butterknife.ButterKnife;",
        "public class A {",
        "  @BindColor(android.R.color.black) @ColorInt int blackColor;",
        "  public A(View view) {",
        "    ButterKnife.bind(this, view);",
        "  }",
        "}"
    ));

    JavaFileObject sourceB = JavaFileObjects.forSourceString("test.B", Joiner.on('\n').join(
        "package test;",
        "import android.support.annotation.ColorInt;",
        "import android.view.View;",
        "import butterknife.BindColor;",
        "import butterknife.ButterKnife;",
        "public class B extends A {",
        "  @BindColor(android.R.color.white) @ColorInt int whiteColor;",
        "  public B(View view) {",
        "    super(view);",
        "    ButterKnife.bind(this, view);",
        "  }",
        "}"
    ));

    JavaFileObject sourceC = JavaFileObjects.forSourceString("test.C", Joiner.on('\n').join(
        "package test;",
        "import android.support.annotation.ColorInt;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "import butterknife.BindColor;",
        "import butterknife.ButterKnife;",
        "public class C extends B {",
        "  @BindColor(android.R.color.transparent) @ColorInt int transparentColor;",
        "  @BindView(android.R.id.button1) View button1;",
        "  public C(View view) {",
        "    super(view);",
        "    ButterKnife.bind(this, view);",
        "  }",
        "}"
    ));

    JavaFileObject sourceD = JavaFileObjects.forSourceString("test.D", Joiner.on('\n').join(
        "package test;",
        "import android.support.annotation.ColorInt;",
        "import android.view.View;",
        "import butterknife.BindColor;",
        "import butterknife.ButterKnife;",
        "public class D extends C {",
        "  @BindColor(android.R.color.darker_gray) @ColorInt int grayColor;",
        "  public D(View view) {",
        "    super(view);",
        "    ButterKnife.bind(this, view);",
        "  }",
        "}"
    ));

    JavaFileObject sourceE = JavaFileObjects.forSourceString("test.E", Joiner.on('\n').join(
        "package test;",
        "import android.support.annotation.ColorInt;",
        "import android.view.View;",
        "import butterknife.BindColor;",
        "import butterknife.ButterKnife;",
        "public class E extends C {",
        "  @BindColor(android.R.color.background_dark) @ColorInt int backgroundDarkColor;",
        "  public E(View view) {",
        "    super(view);",
        "    ButterKnife.bind(this, view);",
        "  }",
        "}"
    ));

    JavaFileObject sourceF = JavaFileObjects.forSourceString("test.F", Joiner.on('\n').join(
        "package test;",
        "import android.support.annotation.ColorInt;",
        "import android.view.View;",
        "import butterknife.BindColor;",
        "import butterknife.ButterKnife;",
        "public class F extends D {",
        "  @BindColor(android.R.color.background_light) @ColorInt int backgroundLightColor;",
        "  public F(View view) {",
        "    super(view);",
        "    ButterKnife.bind(this, view);",
        "  }",
        "}"
    ));

    JavaFileObject sourceG = JavaFileObjects.forSourceString("test.G", Joiner.on('\n').join(
        "package test;",
        "import android.support.annotation.ColorInt;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "import butterknife.BindColor;",
        "import butterknife.ButterKnife;",
        "import butterknife.OnClick;",
        "public class G extends E {",
        "  @BindColor(android.R.color.darker_gray) @ColorInt int grayColor;",
        "  @BindView(android.R.id.button2) View button2;",
        "  public G(View view) {",
        "    super(view);",
        "    ButterKnife.bind(this, view);",
        "  }",
        "  @OnClick(android.R.id.content) public void onClick() {",
        "  }",
        "}"
    ));

    JavaFileObject sourceH = JavaFileObjects.forSourceString("test.H", Joiner.on('\n').join(
        "package test;",
        "import android.support.annotation.ColorInt;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "import butterknife.BindColor;",
        "import butterknife.ButterKnife;",
        "public class H extends G {",
        "  @BindColor(android.R.color.primary_text_dark) @ColorInt int grayColor;",
        "  @BindView(android.R.id.button3) View button3;",
        "  public H(View view) {",
        "    super(view);",
        "    ButterKnife.bind(this, view);",
        "  }",
        "}"
    ));

    JavaFileObject expectedSourceA = JavaFileObjects.forSourceString("test/A$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.content.res.Resources;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.Utils;",
            "import butterknife.internal.ViewBinder;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "import java.lang.SuppressWarnings;",
            "public class A$$ViewBinder<T extends A> implements ViewBinder<T> {",
            "  @Override",
            "  @SuppressWarnings(\"ResourceType\")",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    Context context = finder.getContext(source);",
            "    Resources res = context.getResources();",
            "    Resources.Theme theme = context.getTheme();",
            "    target.blackColor = Utils.getColor(res, theme, 17170444);",
            "    return Unbinder.EMPTY;",
            "  }",
            "}"
      ));

    JavaFileObject expectedSourceB = JavaFileObjects.forSourceString("test/B$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.content.res.Resources;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.Utils;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "import java.lang.SuppressWarnings;",
            "public class B$$ViewBinder<T extends B> extends A$$ViewBinder<T> {",
            "  @Override",
            "  @SuppressWarnings(\"ResourceType\")",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    Unbinder unbinder = super.bind(finder, target, source);",
            "    Context context = finder.getContext(source);",
            "    Resources res = context.getResources();",
            "    Resources.Theme theme = context.getTheme();",
            "    target.whiteColor = Utils.getColor(res, theme, 17170443);",
            "    return unbinder;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSourceC = JavaFileObjects.forSourceString("test/C$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.content.res.Resources;",
            "import android.view.View;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.Utils;",
            "import java.lang.IllegalStateException;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "import java.lang.SuppressWarnings;",
            "public class C$$ViewBinder<T extends C> extends B$$ViewBinder<T> {",
            "  @Override",
            "  @SuppressWarnings(\"ResourceType\")",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    super.bind(finder, target, source);",
            "    InnerUnbinder unbinder = createUnbinder(target);",
            "    View view;",
            "    view = finder.findRequiredView(source, 16908313, \"field 'button1'\");",
            "    target.button1 = view;",
            "    Context context = finder.getContext(source);",
            "    Resources res = context.getResources();",
            "    Resources.Theme theme = context.getTheme();",
            "    target.transparentColor = Utils.getColor(res, theme, 17170445);",
            "    return unbinder;",
            "  }",
            "  protected InnerUnbinder<T> createUnbinder(T target) {",
            "    return new InnerUnbinder(target);",
            "  }",
            "  protected static class InnerUnbinder<T extends C> implements Unbinder {",
            "    private T target;",
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
            "      target.button1 = null;",
            "    }",
            "  }",
            "}"
        ));

    JavaFileObject expectedSourceD = JavaFileObjects.forSourceString("test/D$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.content.res.Resources;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.Utils;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "import java.lang.SuppressWarnings;",
            "public class D$$ViewBinder<T extends D> extends C$$ViewBinder<T> {",
            "  @Override",
            "  @SuppressWarnings(\"ResourceType\")",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    Unbinder unbinder = super.bind(finder, target, source);",
            "    Context context = finder.getContext(source);",
            "    Resources res = context.getResources();",
            "    Resources.Theme theme = context.getTheme();",
            "    target.grayColor = Utils.getColor(res, theme, 17170432);",
            "    return unbinder;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSourceE = JavaFileObjects.forSourceString("test/E$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.content.res.Resources;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.Utils;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "import java.lang.SuppressWarnings;",
            "public class E$$ViewBinder<T extends E> extends C$$ViewBinder<T> {",
            "  @Override",
            "  @SuppressWarnings(\"ResourceType\")",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    Unbinder unbinder = super.bind(finder, target, source);",
            "    Context context = finder.getContext(source);",
            "    Resources res = context.getResources();",
            "    Resources.Theme theme = context.getTheme();",
            "    target.backgroundDarkColor = Utils.getColor(res, theme, 17170446);",
            "    return unbinder;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSourceF = JavaFileObjects.forSourceString("test/F$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.content.res.Resources;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.Utils;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "import java.lang.SuppressWarnings;",
            "public class F$$ViewBinder<T extends F> extends D$$ViewBinder<T> {",
            "  @Override",
            "  @SuppressWarnings(\"ResourceType\")",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    Unbinder unbinder = super.bind(finder, target, source);",
            "    Context context = finder.getContext(source);",
            "    Resources res = context.getResources();",
            "    Resources.Theme theme = context.getTheme();",
            "    target.backgroundLightColor = Utils.getColor(res, theme, 17170447);",
            "    return unbinder;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSourceG = JavaFileObjects.forSourceString("test/G$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.content.res.Resources;",
            "import android.view.View;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.DebouncingOnClickListener;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.Utils;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "import java.lang.SuppressWarnings;",
            "public class G$$ViewBinder<T extends G> extends E$$ViewBinder<T> {",
            "  @Override",
            "  @SuppressWarnings(\"ResourceType\")",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    InnerUnbinder unbinder = (InnerUnbinder) super.bind(finder, target, source);",
            "    View view;",
            "    view = finder.findRequiredView(source, 16908314, \"field 'button2'\");",
            "    target.button2 = view;",
            "    view = finder.findRequiredView(source, 16908290, \"method 'onClick'\");",
            "    unbinder.view16908290 = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override",
            "      public void doClick(View p0) {",
            "        target.onClick();",
            "      }",
            "    });",
            "    Context context = finder.getContext(source);",
            "    Resources res = context.getResources();",
            "    Resources.Theme theme = context.getTheme();",
            "    target.grayColor = Utils.getColor(res, theme, 17170432);",
            "    return unbinder;",
            "  }",
            "  @Override",
            "  protected InnerUnbinder<T> createUnbinder(T target) {",
            "    return new InnerUnbinder(target);",
            "  }",
            "  protected static class InnerUnbinder<T extends G> extends C$$ViewBinder.InnerUnbinder<T> {",
            "    View view16908290;",
            "    protected InnerUnbinder(T target) {",
            "      super(target);",
            "    }",
            "    @Override",
            "    protected void unbind(T target) {",
            "      super.unbind(target);",
            "      target.button2 = null;",
            "      view16908290.setOnClickListener(null);",
            "    }",
            "  }",
            "}"
        ));

    JavaFileObject expectedSourceH = JavaFileObjects.forSourceString("test/H$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.content.res.Resources;",
            "import android.view.View;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.Utils;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "import java.lang.SuppressWarnings;",
            "public class H$$ViewBinder<T extends H> extends G$$ViewBinder<T> {",
            "  @Override",
            "  @SuppressWarnings(\"ResourceType\")",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    InnerUnbinder unbinder = (InnerUnbinder) super.bind(finder, target, source);",
            "    View view;",
            "    view = finder.findRequiredView(source, 16908315, \"field 'button3'\");",
            "    target.button3 = view;",
            "    Context context = finder.getContext(source);",
            "    Resources res = context.getResources();",
            "    Resources.Theme theme = context.getTheme();",
            "    target.grayColor = Utils.getColor(res, theme, 17170433);",
            "    return unbinder;",
            "  }",
            "  @Override",
            "  protected InnerUnbinder<T> createUnbinder(T target) {",
            "    return new InnerUnbinder(target);",
            "  }",
            "  protected static class InnerUnbinder<T extends H> extends G$$ViewBinder.InnerUnbinder<T> {",
            "    protected InnerUnbinder(T target) {",
            "      super(target);",
            "    }",
            "    @Override",
            "    protected void unbind(T target) {",
            "      super.unbind(target);",
            "      target.button3 = null;",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSources())
        .that(asList(sourceA,
            sourceB,
            sourceC,
            sourceD,
            sourceE,
            sourceF,
            sourceG,
            sourceH))
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSourceA,
            expectedSourceB,
            expectedSourceC,
            expectedSourceD,
            expectedSourceE,
            expectedSourceF,
            expectedSourceG,
            expectedSourceH);
  }
}
