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
        ""
            + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    InnerUnbinder unbinder = createUnbinder(target);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "    });\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  protected InnerUnbinder<T> createUnbinder(T target) {\n"
        + "    return new InnerUnbinder(target);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    private T target;\n"
        + "    View view1;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      this.target = target;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public final void unbind() {\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      unbind(target);\n"
        + "      target = null;\n"
        + "    }\n"
        + "    protected void unbind(T target) {\n"
        + "      view1.setOnClickListener(null);\n"
        + "    }\n"
        + "  }\n"
        + "}");

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
                "import butterknife.Bind;",
                "import butterknife.ButterKnife;",
                "import butterknife.OnClick;",
                "import butterknife.OnLongClick;",
                "import butterknife.Unbinder;",
                "public class Test extends Fragment {",
                "  @Bind(1) View view;",
                "  @Bind(2) View view2;",
                "  @OnClick(1) void doStuff() {",
                "  }",
                "  @OnLongClick(1) boolean doMoreStuff() { return false; }",
                "}"
            ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    InnerUnbinder unbinder = createUnbinder(target);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"field 'view', method 'doStuff', and method 'doMoreStuff'\");\n"
        + "    target.view = view;\n"
        + "    unbinder.view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "    });\n"
        + "    view.setOnLongClickListener(new View.OnLongClickListener() {\n"
        + "      @Override\n"
        + "      public boolean onLongClick(View p0) {\n"
        + "        return target.doMoreStuff();\n"
        + "      }\n"
        + "    });\n"
        + "    view = finder.findRequiredView(source, 2, \"field 'view2'\");\n"
        + "    target.view2 = view;\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  protected InnerUnbinder<T> createUnbinder(T target) {\n"
        + "    return new InnerUnbinder(target);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    private T target;\n"
        + "    View view1;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      this.target = target;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public final void unbind() {\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      unbind(target);\n"
        + "      target = null;\n"
        + "    }\n"
        + "    protected void unbind(T target) {\n"
        + "      view1.setOnClickListener(null);\n"
        + "      view1.setOnLongClickListener(null);\n"
        + "      target.view = null;\n"
        + "      target.view2 = null;\n"
        + "    }\n"
        + "  }\n"
        + "}");

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
        ""
            + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    InnerUnbinder unbinder = createUnbinder(target);\n"
        + "    View view;\n"
        + "    view = finder.findOptionalView(source, 1, null);\n"
        + "    if (view != null) {\n"
        + "      unbinder.view1 = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.doStuff();\n"
        + "        }\n"
        + "      });\n"
        + "    }\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  protected InnerUnbinder<T> createUnbinder(T target) {\n"
        + "    return new InnerUnbinder(target);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    private T target;\n"
        + "    View view1;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      this.target = target;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public final void unbind() {\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      unbind(target);\n"
        + "      target = null;\n"
        + "    }\n"
        + "    protected void unbind(T target) {\n"
        + "      if (view1 != null) {\n"
        + "        view1.setOnClickListener(null);\n"
        + "      }\n"
        + "    }\n"
        + "  }\n"
        + "}");

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
        ""
            + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class TestOne$$ViewBinder<T extends TestOne> extends Test$$ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    InnerUnbinder unbinder = (InnerUnbinder) super.bind(finder, target, source);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff2'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff2();\n"
        + "      }\n"
        + "    });\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  @Override\n"
        + "  protected InnerUnbinder<T> createUnbinder(T target) {\n"
        + "    return new InnerUnbinder(target);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends TestOne> extends Test$$ViewBinder.InnerUnbinder<T> {\n"
        + "    View view1;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      super(target);\n"
        + "    }\n"
        + "    @Override\n"
        + "    protected void unbind(T target) {\n"
        + "      super.unbind(target);\n"
        + "      view1.setOnClickListener(null);\n"
        + "    }\n"
        + "  }\n"
        + "}\n");

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestOne$$ViewBinder",
        ""
            + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    InnerUnbinder unbinder = createUnbinder(target);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff1'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff1();\n"
        + "      }\n"
        + "    });\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  protected InnerUnbinder<T> createUnbinder(T target) {\n"
        + "    return new InnerUnbinder(target);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    private T target;\n"
        + "    View view1;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      this.target = target;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public final void unbind() {\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      unbind(target);\n"
        + "      target = null;\n"
        + "    }\n"
        + "    protected void unbind(T target) {\n"
        + "      view1.setOnClickListener(null);\n"
        + "    }\n"
        + "  }\n"
        + "}\n");

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
        ""
            + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class TestOne$$ViewBinder<T extends TestOne> extends Test$$ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    InnerUnbinder unbinder = (InnerUnbinder) super.bind(finder, target, source);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff2'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff2();\n"
        + "      }\n"
        + "    });\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  @Override\n"
        + "  protected InnerUnbinder<T> createUnbinder(T target) {\n"
        + "    return new InnerUnbinder(target);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends TestOne> extends Test$$ViewBinder.InnerUnbinder<T> {\n"
        + "    View view1;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      super(target);\n"
        + "    }\n"
        + "    @Override\n"
        + "    protected void unbind(T target) {\n"
        + "      super.unbind(target);\n"
        + "      view1.setOnClickListener(null);\n"
        + "    }\n"
        + "  }\n"
        + "}\n");

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestOne$$ViewBinder",
        ""
            + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    InnerUnbinder unbinder = createUnbinder(target);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff1'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff1();\n"
        + "      }\n"
        + "    });\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  protected InnerUnbinder<T> createUnbinder(T target) {\n"
        + "    return new InnerUnbinder(target);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    private T target;\n"
        + "    View view1;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      this.target = target;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public final void unbind() {\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      unbind(target);\n"
        + "      target = null;\n"
        + "    }\n"
        + "    protected void unbind(T target) {\n"
        + "      view1.setOnClickListener(null);\n"
        + "    }\n"
        + "  }\n"
        + "}\n");

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
        ""
            + "package test;\n"
            + "import android.view.View;\n"
            + "import butterknife.Unbinder;\n"
            + "import butterknife.internal.DebouncingOnClickListener;\n"
            + "import butterknife.internal.Finder;\n"
            + "import butterknife.internal.ViewBinder;\n"
            + "import java.lang.IllegalStateException;\n"
            + "import java.lang.Object;\n"
            + "import java.lang.Override;\n"
            + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
            + "  @Override\n"
            + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
            + "    InnerUnbinder unbinder = createUnbinder(target);\n"
            + "    View view;\n"
            + "    view = finder.findRequiredView(source, 1, \"method 'doStuff1'\");\n"
            + "    unbinder.view1 = view;\n"
            + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
            + "      @Override\n"
            + "      public void doClick(View p0) {\n"
            + "        target.doStuff1();\n"
            + "      }\n"
            + "    });\n"
            + "    return unbinder;\n"
            + "  }\n"
            + "  protected InnerUnbinder<T> createUnbinder(T target) {\n"
            + "    return new InnerUnbinder(target);\n"
            + "  }\n"
            + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
            + "    private T target;\n"
            + "    View view1;\n"
            + "    protected InnerUnbinder(T target) {\n"
            + "      this.target = target;\n"
            + "    }\n"
            + "    @Override\n"
            + "    public final void unbind() {\n"
            + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
            + "      unbind(target);\n"
            + "      target = null;\n"
            + "    }\n"
            + "    protected void unbind(T target) {\n"
            + "      view1.setOnClickListener(null);\n"
            + "    }\n"
            + "  }\n"
            + "}");

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/one/TestOne$$ViewBinder",
        ""
            + "package test.one;\n"
            + "import android.view.View;\n"
            + "import butterknife.Unbinder;\n"
            + "import butterknife.internal.DebouncingOnClickListener;\n"
            + "import butterknife.internal.Finder;\n"
            + "import java.lang.Object;\n"
            + "import java.lang.Override;\n"
            + "import test.Test$$ViewBinder;\n"
            + "public class TestOne$$ViewBinder<T extends TestOne> extends Test$$ViewBinder<T> {\n"
            + "  @Override\n"
            + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
            + "    InnerUnbinder unbinder = (InnerUnbinder) super.bind(finder, target, source);\n"
            + "    View view;\n"
            + "    view = finder.findRequiredView(source, 2, \"method 'doStuff2'\");\n"
            + "    unbinder.view2 = view;\n"
            + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
            + "      @Override\n"
            + "      public void doClick(View p0) {\n"
            + "        target.doStuff2();\n"
            + "      }\n"
            + "    });\n"
            + "    return unbinder;\n"
            + "  }\n"
            + "  @Override\n"
            + "  protected InnerUnbinder<T> createUnbinder(T target) {\n"
            + "    return new InnerUnbinder(target);\n"
            + "  }\n"
            + "  protected static class InnerUnbinder<T extends TestOne> extends Test$$ViewBinder.InnerUnbinder<T> {\n"
            + "    View view2;\n"
            + "    protected InnerUnbinder(T target) {\n"
            + "      super(target);\n"
            + "    }\n"
            + "    @Override\n"
            + "    protected void unbind(T target) {\n"
            + "      super.unbind(target);\n"
            + "      view2.setOnClickListener(null);\n"
            + "    }\n"
            + "  }\n"
            + "}\n");

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
        ""
            + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    InnerUnbinder unbinder = createUnbinder(target);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff1'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff1();\n"
        + "      }\n"
        + "    });\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  protected InnerUnbinder<T> createUnbinder(T target) {\n"
        + "    return new InnerUnbinder(target);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    private T target;\n"
        + "    View view1;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      this.target = target;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public final void unbind() {\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      unbind(target);\n"
        + "      target = null;\n"
        + "    }\n"
        + "    protected void unbind(T target) {\n"
        + "      view1.setOnClickListener(null);\n"
        + "    }\n"
        + "  }\n"
        + "}\n");

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestTwo$$ViewBinder",
        ""
            + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class TestTwo$$ViewBinder<T extends TestTwo> extends Test$$ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    InnerUnbinder unbinder = (InnerUnbinder) super.bind(finder, target, source);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff2'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff2();\n"
        + "      }\n"
        + "    });\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  @Override\n"
        + "  protected InnerUnbinder<T> createUnbinder(T target) {\n"
        + "    return new InnerUnbinder(target);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends TestTwo> extends Test$$ViewBinder.InnerUnbinder<T> {\n"
        + "    View view1;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      super(target);\n"
        + "    }\n"
        + "    @Override\n"
        + "    protected void unbind(T target) {\n"
        + "      super.unbind(target);\n"
        + "      view1.setOnClickListener(null);\n"
        + "    }\n"
        + "  }\n"
        + "}\n");

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void fullIntegration() {
    JavaFileObject sourceA = JavaFileObjects.forSourceString("test.A",
        ""
            + "package test;\n"
        + "import android.support.annotation.ColorInt;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindColor;\n"
        + "import butterknife.ButterKnife;\n"
        + "public class A {\n"
        + "  @BindColor(android.R.color.black) @ColorInt int blackColor;\n"
        + "  public A(View view) {\n"
        + "    ButterKnife.bind(this, view);\n"
        + "  }\n"
        + "}\n");

    JavaFileObject sourceB = JavaFileObjects.forSourceString("test.B",
        ""
            + "package test;\n"
        + "import android.support.annotation.ColorInt;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindColor;\n"
        + "import butterknife.ButterKnife;\n"
        + "public class B extends A {\n"
        + "  @BindColor(android.R.color.white) @ColorInt int whiteColor;\n"
        + "  public B(View view) {\n"
        + "    super(view);\n"
        + "    ButterKnife.bind(this, view);\n"
        + "  }\n"
        + "}\n");

    JavaFileObject sourceC = JavaFileObjects.forSourceString("test.C",
        ""
            + "package test;\n"
        + "import android.support.annotation.ColorInt;\n"
        + "import android.view.View;\n"
        + "import butterknife.Bind;\n"
        + "import butterknife.BindColor;\n"
        + "import butterknife.ButterKnife;\n"
        + "public class C extends B {\n"
        + "  @BindColor(android.R.color.transparent) @ColorInt int transparentColor;\n"
        + "  @Bind(android.R.id.button1) View button1;\n"
        + "  public C(View view) {\n"
        + "    super(view);\n"
        + "    ButterKnife.bind(this, view);\n"
        + "  }\n"
        + "}\n");

    JavaFileObject sourceD = JavaFileObjects.forSourceString("test.D",
        ""
            + "package test;\n"
        + "import android.support.annotation.ColorInt;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindColor;\n"
        + "import butterknife.ButterKnife;\n"
        + "public class D extends C {\n"
        + "  @BindColor(android.R.color.darker_gray) @ColorInt int grayColor;\n"
        + "  public D(View view) {\n"
        + "    super(view);\n"
        + "    ButterKnife.bind(this, view);\n"
        + "  }\n"
        + "}\n");

    JavaFileObject sourceE = JavaFileObjects.forSourceString("test.E",
        ""
            + "package test;\n"
        + "import android.support.annotation.ColorInt;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindColor;\n"
        + "import butterknife.ButterKnife;\n"
        + "public class E extends C {\n"
        + "  @BindColor(android.R.color.background_dark) @ColorInt int backgroundDarkColor;\n"
        + "  public E(View view) {\n"
        + "    super(view);\n"
        + "    ButterKnife.bind(this, view);\n"
        + "  }\n"
        + "}\n");

    JavaFileObject sourceF = JavaFileObjects.forSourceString("test.F",
        ""
            + "package test;\n"
        + "import android.support.annotation.ColorInt;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindColor;\n"
        + "import butterknife.ButterKnife;\n"
        + "public class F extends D {\n"
        + "  @BindColor(android.R.color.background_light) @ColorInt int backgroundLightColor;\n"
        + "  public F(View view) {\n"
        + "    super(view);\n"
        + "    ButterKnife.bind(this, view);\n"
        + "  }\n"
        + "}\n");

    JavaFileObject sourceG = JavaFileObjects.forSourceString("test.G",
        ""
            + "package test;\n"
        + "import android.support.annotation.ColorInt;\n"
        + "import android.view.View;\n"
        + "import butterknife.Bind;\n"
        + "import butterknife.BindColor;\n"
        + "import butterknife.ButterKnife;\n"
        + "import butterknife.OnClick;\n"
        + "public class G extends E {\n"
        + "  @BindColor(android.R.color.darker_gray) @ColorInt int grayColor;\n"
        + "  @Bind(android.R.id.button2) View button2;\n"
        + "  public G(View view) {\n"
        + "    super(view);\n"
        + "    ButterKnife.bind(this, view);\n"
        + "  }\n"
        + "  @OnClick(android.R.id.content) public void onClick() {\n"
        + "  }\n"
        + "}\n");

    JavaFileObject sourceH = JavaFileObjects.forSourceString("test.H",
        ""
            + "package test;\n"
        + "import android.support.annotation.ColorInt;\n"
        + "import android.view.View;\n"
        + "import butterknife.Bind;\n"
        + "import butterknife.BindColor;\n"
        + "import butterknife.ButterKnife;\n"
        + "public class H extends G {\n"
        + "  @BindColor(android.R.color.primary_text_dark) @ColorInt int grayColor;\n"
        + "  @Bind(android.R.id.button3) View button3;\n"
        + "  public H(View view) {\n"
        + "    super(view);\n"
        + "    ButterKnife.bind(this, view);\n"
        + "  }\n"
        + "}\n");

    JavaFileObject expectedSourceA = JavaFileObjects.forSourceString("test/A$$ViewBinder",
        ""
            + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class A$$ViewBinder<T extends A> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    target.blackColor = Utils.getColor(res, theme, 17170444);\n"
        + "    return Unbinder.EMPTY;\n"
        + "  }\n"
        + "}\n");

    JavaFileObject expectedSourceB = JavaFileObjects.forSourceString("test/B$$ViewBinder",
        ""
            + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class B$$ViewBinder<T extends B> extends A$$ViewBinder<T> {\n"
        + "  @Override\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    Unbinder unbinder = super.bind(finder, target, source);\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    target.whiteColor = Utils.getColor(res, theme, 17170443);\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "}\n");

    JavaFileObject expectedSourceC = JavaFileObjects.forSourceString("test/C$$ViewBinder",
        ""
            + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class C$$ViewBinder<T extends C> extends B$$ViewBinder<T> {\n"
        + "  @Override\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    super.bind(finder, target, source);\n"
        + "    InnerUnbinder unbinder = createUnbinder(target);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 16908313, \"field 'button1'\");\n"
        + "    target.button1 = view;\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    target.transparentColor = Utils.getColor(res, theme, 17170445);\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  protected InnerUnbinder<T> createUnbinder(T target) {\n"
        + "    return new InnerUnbinder(target);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends C> implements Unbinder {\n"
        + "    private T target;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      this.target = target;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public final void unbind() {\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      unbind(target);\n"
        + "      target = null;\n"
        + "    }\n"
        + "    protected void unbind(T target) {\n"
        + "      target.button1 = null;\n"
        + "    }\n"
        + "  }\n"
        + "}\n");

    JavaFileObject expectedSourceD = JavaFileObjects.forSourceString("test/D$$ViewBinder",
        ""
            + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class D$$ViewBinder<T extends D> extends C$$ViewBinder<T> {\n"
        + "  @Override\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    Unbinder unbinder = super.bind(finder, target, source);\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    target.grayColor = Utils.getColor(res, theme, 17170432);\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "}\n");

    JavaFileObject expectedSourceE = JavaFileObjects.forSourceString("test/E$$ViewBinder",
        ""
            + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class E$$ViewBinder<T extends E> extends C$$ViewBinder<T> {\n"
        + "  @Override\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    Unbinder unbinder = super.bind(finder, target, source);\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    target.backgroundDarkColor = Utils.getColor(res, theme, 17170446);\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "}\n");

    JavaFileObject expectedSourceF = JavaFileObjects.forSourceString("test/F$$ViewBinder",
        ""
            + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class F$$ViewBinder<T extends F> extends D$$ViewBinder<T> {\n"
        + "  @Override\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    Unbinder unbinder = super.bind(finder, target, source);\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    target.backgroundLightColor = Utils.getColor(res, theme, 17170447);\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "}\n");

    JavaFileObject expectedSourceG = JavaFileObjects.forSourceString("test/G$$ViewBinder",
        ""
            + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class G$$ViewBinder<T extends G> extends E$$ViewBinder<T> {\n"
        + "  @Override\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    InnerUnbinder unbinder = (InnerUnbinder) super.bind(finder, target, source);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 16908314, \"field 'button2'\");\n"
        + "    target.button2 = view;\n"
        + "    view = finder.findRequiredView(source, 16908290, \"method 'onClick'\");\n"
        + "    unbinder.view16908290 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.onClick();\n"
        + "      }\n"
        + "    });\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    target.grayColor = Utils.getColor(res, theme, 17170432);\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  @Override\n"
        + "  protected InnerUnbinder<T> createUnbinder(T target) {\n"
        + "    return new InnerUnbinder(target);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends G> extends C$$ViewBinder.InnerUnbinder<T> {\n"
        + "    View view16908290;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      super(target);\n"
        + "    }\n"
        + "    @Override\n"
        + "    protected void unbind(T target) {\n"
        + "      super.unbind(target);\n"
        + "      target.button2 = null;\n"
        + "      view16908290.setOnClickListener(null);\n"
        + "    }\n"
        + "  }\n"
        + "}\n");

    JavaFileObject expectedSourceH = JavaFileObjects.forSourceString("test/H$$ViewBinder",
        ""
            + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class H$$ViewBinder<T extends H> extends G$$ViewBinder<T> {\n"
        + "  @Override\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    InnerUnbinder unbinder = (InnerUnbinder) super.bind(finder, target, source);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 16908315, \"field 'button3'\");\n"
        + "    target.button3 = view;\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    target.grayColor = Utils.getColor(res, theme, 17170433);\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  @Override\n"
        + "  protected InnerUnbinder<T> createUnbinder(T target) {\n"
        + "    return new InnerUnbinder(target);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends H> extends G$$ViewBinder.InnerUnbinder<T> {\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      super(target);\n"
        + "    }\n"
        + "    @Override\n"
        + "    protected void unbind(T target) {\n"
        + "      super.unbind(target);\n"
        + "      target.button3 = null;\n"
        + "    }\n"
        + "  }\n"
        + "}\n");

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
