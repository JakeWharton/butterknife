package butterknife;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnClickTest {
  @Test public void onClickBinding() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(1) void doStuff() {}",
        "}"));

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

  @Test public void onClickMultipleBindings() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(1) void doStuff1() {}",
        "  @OnClick(1) void doStuff2() {}",
        "  @OnClick({1, 2}) void doStuff3(View v) {}",
        "}"));

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
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff1', method 'doStuff2', and method 'doStuff3'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff1();\n"
        + "        target.doStuff2();\n"
        + "        target.doStuff3(p0);\n"
        + "      }\n"
        + "    });\n"
        + "    view = finder.findRequiredView(source, 2, \"method 'doStuff3'\");\n"
        + "    unbinder.view2 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff3(p0);\n"
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
        + "    View view2;\n"
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
        + "      view2.setOnClickListener(null);\n"
        + "    }\n"
        + "  }\n"
        + "}");

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void findOnlyCalledOnce() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @Bind(1) View view;",
        "  @OnClick(1) void doStuff() {}",
        "}"));

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
        + "    view = finder.findRequiredView(source, 1, \"field 'view' and method 'doStuff'\");\n"
        + "    target.view = view;\n"
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
        + "      target.view = null;\n"
        + "    }\n"
        + "  }\n"
        + "}");

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void methodVisibility() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(1) public void thing1() {}",
        "  @OnClick(2) void thing2() {}",
        "  @OnClick(3) protected void thing3() {}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError();
  }

  @Test public void methodCastsArgument() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import android.widget.Button;",
        "import android.widget.TextView;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  interface TestInterface {}",
        "  @OnClick(0) void click0() {}",
        "  @OnClick(1) void click1(View view) {}",
        "  @OnClick(2) void click2(TextView view) {}",
        "  @OnClick(3) void click3(Button button) {}",
        "  @OnClick(4) void click4(TestInterface thing) {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.Button;\n"
        + "import android.widget.TextView;\n"
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
        + "    view = finder.findRequiredView(source, 0, \"method 'click0'\");\n"
        + "    unbinder.view0 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click0();\n"
        + "      }\n"
        + "    });\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'click1'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click1(p0);\n"
        + "      }\n"
        + "    });\n"
        + "    view = finder.findRequiredView(source, 2, \"method 'click2'\");\n"
        + "    unbinder.view2 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click2(finder.<TextView>castParam(p0, \"doClick\", 0, \"click2\", 0)\n"
        + "        );\n"
        + "      }\n"
        + "    });\n"
        + "    view = finder.findRequiredView(source, 3, \"method 'click3'\");\n"
        + "    unbinder.view3 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click3(finder.<Button>castParam(p0, \"doClick\", 0, \"click3\", 0)\n"
        + "        );\n"
        + "      }\n"
        + "    });\n"
        + "    view = finder.findRequiredView(source, 4, \"method 'click4'\");\n"
        + "    unbinder.view4 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click4(finder.<Test.TestInterface>castParam(p0, \"doClick\", 0, \"click4\", 0)\n"
        + "        );\n"
        + "      }\n"
        + "    });\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  protected InnerUnbinder<T> createUnbinder(T target) {\n"
        + "    return new InnerUnbinder(target);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    private T target;\n"
        + "    View view0;\n"
        + "    View view1;\n"
        + "    View view2;\n"
        + "    View view3;\n"
        + "    View view4;\n"
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
        + "      view0.setOnClickListener(null);\n"
        + "      view1.setOnClickListener(null);\n"
        + "      view2.setOnClickListener(null);\n"
        + "      view3.setOnClickListener(null);\n"
        + "      view4.setOnClickListener(null);\n"
        + "    }\n"
        + "  }\n"
        + "}");

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void methodWithMultipleIds() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick({1, 2, 3}) void click() {}",
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
        + "    view = finder.findRequiredView(source, 1, \"method 'click'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click();\n"
        + "      }\n"
        + "    });\n"
        + "    view = finder.findRequiredView(source, 2, \"method 'click'\");\n"
        + "    unbinder.view2 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click();\n"
        + "      }\n"
        + "    });\n"
        + "    view = finder.findRequiredView(source, 3, \"method 'click'\");\n"
        + "    unbinder.view3 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click();\n"
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
        + "    View view2;\n"
        + "    View view3;\n"
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
        + "      view2.setOnClickListener(null);\n"
        + "      view3.setOnClickListener(null);\n"
        + "    }\n"
        + "  }\n"
        + "}");

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void nullable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "import butterknife.Optional;",
        "public class Test extends Activity {",
        "  @Optional @OnClick(1) void doStuff() {}",
        "}"));

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

  @Test public void optionalAndRequiredSkipsNullCheck() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "import butterknife.OnClick;",
        "import butterknife.Optional;",
        "public class Test extends Activity {",
        "  @Bind(1) View view;",
        "  @Optional @OnClick(1) void doStuff() {}",
        "}"));

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
        + "    view = finder.findRequiredView(source, 1, \"field 'view'\");\n"
        + "    target.view = view;\n"
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
        + "      target.view = null;\n"
        + "    }\n"
        + "  }\n"
        + "}");

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void failsInJavaPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package java.test;",
        "import butterknife.OnClick;",
        "public class Test {",
        "  @OnClick(1) void doStuff() {}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick-annotated class incorrectly in Java framework package. (java.test.Test)")
        .in(source).onLine(4);
  }

  @Test public void failsInAndroidPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package android.test;",
        "import butterknife.OnClick;",
        "public class Test {",
        "  @OnClick(1) void doStuff() {}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick-annotated class incorrectly in Android framework package. (android.test.Test)")
        .in(source).onLine(4);
  }

  @Test public void failsIfHasReturnType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(1)",
        "  public String doStuff() {",
        "  }",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@OnClick methods must have a 'void' return type. (test.Test.doStuff)")
        .in(source).onLine(6);
  }

  @Test public void failsIfPrivateMethod() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(1)",
        "  private void doStuff() {",
        "  }",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@OnClick methods must not be private or static. (test.Test.doStuff)")
        .in(source).onLine(6);
  }

  @Test public void failsIfStatic() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(1)",
        "  public static void doStuff() {",
        "  }",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@OnClick methods must not be private or static. (test.Test.doStuff)")
        .in(source).onLine(6);
  }

  @Test public void failsIfParameterNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(1)",
        "  public void doStuff(String thing) {",
        "  }",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(Joiner.on('\n').join(
            "Unable to match @OnClick method arguments. (test.Test.doStuff)",
            "  ",
            "    Parameter #1: java.lang.String",
            "      did not match any listener parameters",
            "  ",
            "  Methods may have up to 1 parameter(s):",
            "  ",
            "    android.view.View",
            "  ",
            "  These may be listed in any order but will be searched for from top to bottom."))
        .in(source).onLine(6);
  }

  @Test public void failsIfMoreThanOneParameter() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(1)",
        "  public void doStuff(View thing, View otherThing) {",
        "  }",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick methods can have at most 1 parameter(s). (test.Test.doStuff)")
        .in(source).onLine(7);
  }

  @Test public void failsIfInInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import butterknife.OnClick;",
        "public interface Test {",
        "  @OnClick(1)",
        "  void doStuff();",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick methods may only be contained in classes. (test.Test.doStuff)")
        .in(source).onLine(3);
  }

  @Test public void failsIfHasDuplicateIds() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick({1, 2, 3, 1})",
        "  void doStuff() {",
        "  }",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick annotation for method contains duplicate ID 1. (test.Test.doStuff)")
        .in(source).onLine(6);
  }
}
