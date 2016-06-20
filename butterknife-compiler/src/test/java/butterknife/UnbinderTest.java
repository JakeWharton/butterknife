package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class UnbinderTest {
  @Test public void multipleBindings() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.support.v4.app.Fragment;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "import butterknife.ButterKnife;\n"
        + "import butterknife.OnClick;\n"
        + "import butterknife.OnLongClick;\n"
        + "import butterknife.Unbinder;\n"
        + "public class Test extends Fragment {\n"
        + "  @BindView(1) View view;\n"
        + "  @BindView(2) View view2;\n"
        + "  @OnClick(1) void doStuff() {}\n"
        + "  @OnLongClick(1) boolean doMoreStuff() { return false; }\n"
        + "}"
    );

    JavaFileObject binderSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding<T extends Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  private View view1;\n"
        + "  public Test_ViewBinding(final T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"field 'view', method 'doStuff', and method 'doMoreStuff'\");\n"
        + "    target.view = view;\n"
        + "    view1 = view;\n"
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
        + "    target.view2 = finder.findRequiredView(source, 2, \"field 'view2'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    T target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target.view = null;\n"
        + "    target.view2 = null;\n"
        + "    view1.setOnClickListener(null);\n"
        + "    view1.setOnLongClickListener(null);\n"
        + "    view1 = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binderSource, bindingSource);
  }

  @Test public void unbinderRespectsNullable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.support.v4.app.Fragment;\n"
        + "import butterknife.ButterKnife;\n"
        + "import butterknife.OnClick;\n"
        + "import butterknife.Optional;\n"
        + "import butterknife.Unbinder;\n"
        + "public class Test extends Fragment {\n"
        + "  @Optional @OnClick(1) void doStuff() {}\n"
        + "}"
    );

    JavaFileObject binderSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding<T extends Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  private View view1;\n"
        + "  public Test_ViewBinding(final T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = finder.findOptionalView(source, 1);\n"
        + "    if (view != null) {\n"
        + "      view1 = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.doStuff();\n"
        + "        }\n"
        + "      });\n"
        + "    }\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    if (view1 != null) {\n"
        + "      view1.setOnClickListener(null);\n"
        + "      view1 = null;\n"
        + "    }\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binderSource, bindingSource);
  }

  @Test public void childBindsSecondUnbinder() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.support.v4.app.Fragment;\n"
        + "import butterknife.ButterKnife;\n"
        + "import butterknife.OnClick;\n"
        + "import butterknife.Unbinder;\n"
        + "public class Test extends Fragment {\n"
        + "  @OnClick(1) void doStuff1() {}\n"
        + "}\n"
        + "class TestOne extends Test {\n"
        + "  @OnClick(1) void doStuff2() {}\n"
        + "}\n"
        + "class TestTwo extends Test {}"
    );

    JavaFileObject binder1Source = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binding1Source = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding<T extends Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  private View view1;\n"
        + "  public Test_ViewBinding(final T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff1'\");\n"
        + "    view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff1();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    view1.setOnClickListener(null);\n"
        + "    view1 = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binder2Source = JavaFileObjects.forSourceString("test/TestOne_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class TestOne_ViewBinder<T extends TestOne> extends Test_ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new TestOne_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binding2Source = JavaFileObjects.forSourceString("test/TestOne_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class TestOne_ViewBinding<T extends TestOne> extends Test_ViewBinding<T> {\n"
        + "  private View view1;\n"
        + "  public TestOne_ViewBinding(final T target, Finder finder, Object source) {\n"
        + "    super(target, finder, source);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff2'\");\n"
        + "    view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff2();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    super.unbind();\n"
        + "    view1.setOnClickListener(null);\n"
        + "    view1 = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binder1Source, binding1Source, binder2Source, binding2Source);
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

    JavaFileObject binder1Source = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binding1Source = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding<T extends Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  private View view1;\n"
        + "  public Test_ViewBinding(final T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff1'\");\n"
        + "    view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff1();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    view1.setOnClickListener(null);\n"
        + "    view1 = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binder2Source = JavaFileObjects.forSourceString("test/TestOne_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class TestOne_ViewBinder<T extends TestOne> extends Test_ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new TestOne_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binding2Source = JavaFileObjects.forSourceString("test/TestOne_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class TestOne_ViewBinding<T extends TestOne> extends Test_ViewBinding<T> {\n"
        + "  private View view1;\n"
        + "  public TestOne_ViewBinding(final T target, Finder finder, Object source) {\n"
        + "    super(target, finder, source);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff2'\");\n"
        + "    view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff2();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    super.unbind();\n"
        + "    view1.setOnClickListener(null);\n"
        + "    view1 = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binder1Source, binding1Source, binder2Source, binding2Source);
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

    JavaFileObject binder1Source = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binding1Source = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding<T extends Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  private View view1;\n"
        + "  public Test_ViewBinding(final T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff1'\");\n"
        + "    view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff1();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    view1.setOnClickListener(null);\n"
        + "    view1 = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binder2Source = JavaFileObjects.forSourceString("test/one/TestOne_ViewBinder", ""
        + "package test.one;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import test.Test_ViewBinder;\n"
        + "public class TestOne_ViewBinder<T extends TestOne> extends Test_ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new TestOne_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binding2Source = JavaFileObjects.forSourceString("test/one/TestOne_ViewBinding", ""
        + "package test.one;\n"
        + "import android.view.View;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import test.Test_ViewBinding;\n"
        + "public class TestOne_ViewBinding<T extends TestOne> extends Test_ViewBinding<T> {\n"
        + "  private View view2;\n"
        + "  public TestOne_ViewBinding(final T target, Finder finder, Object source) {\n"
        + "    super(target, finder, source);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 2, \"method 'doStuff2'\");\n"
        + "    view2 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff2();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    super.unbind();\n"
        + "    view2.setOnClickListener(null);\n"
        + "    view2 = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources()).that(asList(source1, source2))
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binder1Source, binding1Source, binder2Source, binding2Source);
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

    JavaFileObject binder1Source = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binding1Source = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding<T extends Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  private View view1;\n"
        + "  public Test_ViewBinding(final T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff1'\");\n"
        + "    view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff1();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    view1.setOnClickListener(null);\n"
        + "    view1 = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binder2Source = JavaFileObjects.forSourceString("test/TestTwo_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class TestTwo_ViewBinder<T extends TestTwo> extends Test_ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new TestTwo_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binding2Source = JavaFileObjects.forSourceString("test/TestTwo_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class TestTwo_ViewBinding<T extends TestTwo> extends Test_ViewBinding<T> {\n"
        + "  private View view1;\n"
        + "  public TestTwo_ViewBinding(final T target, Finder finder, Object source) {\n"
        + "    super(target, finder, source);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff2'\");\n"
        + "    view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff2();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    super.unbind();\n"
        + "    view1.setOnClickListener(null);\n"
        + "    view1 = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binder1Source, binding1Source, binder2Source, binding2Source);
  }

  @Test public void fullIntegration() {
    JavaFileObject sourceA = JavaFileObjects.forSourceString("test.A", ""
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

    JavaFileObject sourceB = JavaFileObjects.forSourceString("test.B", ""
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

    JavaFileObject sourceC = JavaFileObjects.forSourceString("test.C", ""
        + "package test;\n"
        + "import android.support.annotation.ColorInt;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "import butterknife.BindColor;\n"
        + "import butterknife.ButterKnife;\n"
        + "public class C extends B {\n"
        + "  @BindColor(android.R.color.transparent) @ColorInt int transparentColor;\n"
        + "  @BindView(android.R.id.button1) View button1;\n"
        + "  public C(View view) {\n"
        + "    super(view);\n"
        + "    ButterKnife.bind(this, view);\n"
        + "  }\n"
        + "}\n");

    JavaFileObject sourceD = JavaFileObjects.forSourceString("test.D", ""
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

    JavaFileObject sourceE = JavaFileObjects.forSourceString("test.E", ""
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

    JavaFileObject sourceF = JavaFileObjects.forSourceString("test.F", ""
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

    JavaFileObject sourceG = JavaFileObjects.forSourceString("test.G", ""
        + "package test;\n"
        + "import android.support.annotation.ColorInt;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "import butterknife.BindColor;\n"
        + "import butterknife.ButterKnife;\n"
        + "import butterknife.OnClick;\n"
        + "public class G extends E {\n"
        + "  @BindColor(android.R.color.darker_gray) @ColorInt int grayColor;\n"
        + "  @BindView(android.R.id.button2) View button2;\n"
        + "  public G(View view) {\n"
        + "    super(view);\n"
        + "    ButterKnife.bind(this, view);\n"
        + "  }\n"
        + "  @OnClick(android.R.id.content) public void onClick() {\n"
        + "  }\n"
        + "}\n");

    JavaFileObject sourceH = JavaFileObjects.forSourceString("test.H", ""
        + "package test;\n"
        + "import android.support.annotation.ColorInt;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "import butterknife.BindColor;\n"
        + "import butterknife.ButterKnife;\n"
        + "public class H extends G {\n"
        + "  @BindColor(android.R.color.primary_text_dark) @ColorInt int grayColor;\n"
        + "  @BindView(android.R.id.button3) View button3;\n"
        + "  public H(View view) {\n"
        + "    super(view);\n"
        + "    ButterKnife.bind(this, view);\n"
        + "  }\n"
        + "}\n");

    JavaFileObject binderASource = JavaFileObjects.forSourceString("test/A_ViewBinder", ""
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
        + "public class A_ViewBinder<T extends A> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    bindToTarget(target, res, theme);\n"
        + "    return Unbinder.EMPTY;\n"
        + "  }\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public static void bindToTarget(A target, Resources res, Resources.Theme theme) {\n"
        + "    target.blackColor = Utils.getColor(res, theme, 17170444);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binderBSource = JavaFileObjects.forSourceString("test/B_ViewBinder", ""
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class B_ViewBinder<T extends B> extends A_ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    bindToTarget(target, res, theme);\n"
        + "    return Unbinder.EMPTY;\n"
        + "  }\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public static void bindToTarget(B target, Resources res, Resources.Theme theme) {\n"
        + "    A_ViewBinder.bindToTarget(target, res, theme);\n"
        + "    target.whiteColor = Utils.getColor(res, theme, 17170443);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binderCSource = JavaFileObjects.forSourceString("test/C_ViewBinder", ""
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class C_ViewBinder<T extends C> extends B_ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    return new C_ViewBinding<>(target, finder, source, res, theme);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingCSource = JavaFileObjects.forSourceString("test/C_ViewBinding", ""
        + "package test;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class C_ViewBinding<T extends C> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public C_ViewBinding(T target, Finder finder, Object source, Resources res, Resources.Theme theme) {\n"
        + "    this.target = target;\n"
        + "    B_ViewBinder.bindToTarget(target, res, theme);\n"
        + "    target.button1 = finder.findRequiredView(source, 16908313, \"field 'button1'\");\n"
        + "    target.transparentColor = Utils.getColor(res, theme, 17170445);\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    T target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target.button1 = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binderDSource = JavaFileObjects.forSourceString("test/D_ViewBinder", ""
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class D_ViewBinder<T extends D> extends C_ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    return new D_ViewBinding<>(target, finder, source, res, theme);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingDSource = JavaFileObjects.forSourceString("test/D_ViewBinding", ""
        + "package test;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class D_ViewBinding<T extends D> extends C_ViewBinding<T> {\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public D_ViewBinding(T target, Finder finder, Object source, Resources res, Resources.Theme theme) {\n"
        + "    super(target, finder, source, res, theme);\n"
        + "    target.grayColor = Utils.getColor(res, theme, 17170432);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binderESource = JavaFileObjects.forSourceString("test/E_ViewBinder", ""
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class E_ViewBinder<T extends E> extends C_ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    return new E_ViewBinding<>(target, finder, source, res, theme);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingESource = JavaFileObjects.forSourceString("test/E_ViewBinding", ""
        + "package test;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class E_ViewBinding<T extends E> extends C_ViewBinding<T> {\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public E_ViewBinder(T target, Finder finder, Object source, Resources res, Resources.Theme theme) {\n"
        + "    super(target, finder, source, res, theme);\n"
        + "    target.backgroundDarkColor = Utils.getColor(res, theme, 17170446);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binderFSource = JavaFileObjects.forSourceString("test/F_ViewBinder", ""
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class F_ViewBinder<T extends F> extends D_ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    return new F_ViewBinding<>(target, finder, source, res, theme);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingFSource = JavaFileObjects.forSourceString("test/F_ViewBinding", ""
        + "package test;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class F_ViewBinding<T extends F> extends D_ViewBinding<T> {\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public F_ViewBinding(T target, Finder finder, Object source, Resources res, Resources.Theme theme) {\n"
        + "    super(target, finder, source, res, theme);\n"
        + "    target.backgroundLightColor = Utils.getColor(res, theme, 17170447);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binderGSource = JavaFileObjects.forSourceString("test/G_ViewBinder", ""
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class G_ViewBinder<T extends G> extends E_ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    return new G_ViewBinding<>(target, finder, source, res, theme);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingGSource = JavaFileObjects.forSourceString("test/G_ViewBinding", ""
        + "package test;\n"
        + "import android.content.res.Resources;\n"
        + "import android.view.View;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class G_ViewBinding<T extends G> extends E_ViewBinding<T> {\n"
        + "  private View view16908290;\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public G_ViewBinding(final T target, Finder finder, Object source, Resources res, Resources.Theme theme) {\n"
        + "    super(target, finder, source, res, theme);\n"
        + "    View view;\n"
        + "    target.button2 = finder.findRequiredView(source, 16908314, \"field 'button2'\");\n"
        + "    view = finder.findRequiredView(source, 16908290, \"method 'onClick'\");\n"
        + "    view16908290 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.onClick();\n"
        + "      }\n"
        + "    });\n"
        + "    target.grayColor = Utils.getColor(res, theme, 17170432);\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    T target = this.target;\n"
        + "    super.unbind();\n"
        + "    target.button2 = null;\n"
        + "    view16908290.setOnClickListener(null);\n"
        + "    view16908290 = null;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binderHSource = JavaFileObjects.forSourceString("test/H_ViewBinder", ""
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class H_ViewBinder<T extends H> extends G_ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    return new H_ViewBinding<>(target, finder, source, res, theme);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingHSource = JavaFileObjects.forSourceString("test/H_ViewBinding", ""
        + "package test;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class H_ViewBinding<T extends H> extends G_ViewBinding<T> {\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public H_ViewBinding(T target, Finder finder, Object source, Resources res, Resources.Theme theme) {\n"
        + "    super(target, finder, source, res, theme);\n"
        + "    target.button3 = finder.findRequiredView(source, 16908315, \"field 'button3'\");\n"
        + "    target.grayColor = Utils.getColor(res, theme, 17170433);\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    T target = this.target;\n"
        + "    super.unbind();\n"
        + "    target.button3 = null;\n"
        + "  }\n"
        + "}"
    );

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
        .generatesSources(binderASource,
            binderBSource,
            binderCSource,
            bindingCSource,
            binderDSource,
            bindingDSource,
            binderESource,
            bindingESource,
            binderFSource,
            bindingFSource,
            binderGSource,
            bindingGSource,
            binderHSource,
            bindingHSource);
  }
}
