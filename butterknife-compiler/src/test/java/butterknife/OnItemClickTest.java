package butterknife;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/** This augments {@link OnClickTest} with tests that exercise callbacks with parameters. */
public class OnItemClickTest {
  @Test public void onItemClickBinding() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;"
        + "import android.app.Activity;"
        + "import butterknife.OnItemClick;"
        + "public class Test extends Activity {"
        + "  @OnItemClick(1) void doStuff() {}"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    InnerUnbinder unbinder = new InnerUnbinder(target);\n"
        + "    bindToTarget(target, finder, source, unbinder);\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  protected static void bindToTarget(final Test target, Finder finder, Object source, InnerUnbinder unbinder) {\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {\n"
        + "      @Override\n"
        + "      public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    View view1;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      this.target = target;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      T target = this.target;\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      ((AdapterView<?>) view1).setOnItemClickListener(null);\n"
        + "      view1 = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void onItemClickBindingWithParameters() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import butterknife.OnItemClick;\n"
        + "public class Test extends Activity {\n"
        + "  @OnItemClick(1) void doStuff(\n"
        + "    AdapterView<?> parent,\n"
        + "    View view,\n"
        + "    int position,\n"
        + "    long id\n"
        + "  ) {}\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    InnerUnbinder unbinder = new InnerUnbinder(target);\n"
        + "    bindToTarget(target, finder, source, unbinder);\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  protected static void bindToTarget(final Test target, Finder finder, Object source, InnerUnbinder unbinder) {\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {\n"
        + "      @Override\n"
        + "      public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        target.doStuff(p0, p1, p2, p3);\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    View view1;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      this.target = target;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      T target = this.target;\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      ((AdapterView<?>) view1).setOnItemClickListener(null);\n"
        + "      view1 = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void onItemClickBindingWithParameterSubset() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import android.view.View;\n"
        + "import android.widget.ListView;\n"
        + "import butterknife.OnItemClick;\n"
        + "public class Test extends Activity {\n"
        + "  @OnItemClick(1) void doStuff(\n"
        + "    ListView parent,\n"
        + "    int position\n"
        + "  ) {}\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import android.widget.ListView;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    InnerUnbinder unbinder = new InnerUnbinder(target);\n"
        + "    bindToTarget(target, finder, source, unbinder);\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  protected static void bindToTarget(final Test target, Finder finder, Object source, InnerUnbinder unbinder) {\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {\n"
        + "      @Override\n"
        + "      public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        target.doStuff(finder.<ListView>castParam(p0, \"onItemClick\", 0, \"doStuff\", 0)\n"
        + "        , p2);\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    View view1;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      this.target = target;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      T target = this.target;\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      ((AdapterView<?>) view1).setOnItemClickListener(null);\n"
        + "      view1 = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void onItemClickBindingWithParameterSubsetAndGenerics() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import android.view.View;\n"
        + "import android.widget.ListView;\n"
        + "import butterknife.OnItemClick;\n"
        + "public class Test<T extends ListView> extends Activity {\n"
        + "  @OnItemClick(1) void doStuff(\n"
        + "    T parent,\n"
        + "    int position\n"
        + "  ) {}\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import android.widget.ListView;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    InnerUnbinder unbinder = new InnerUnbinder(target);\n"
        + "    bindToTarget(target, finder, source, unbinder);\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  protected static void bindToTarget(final Test target, Finder finder, Object source, InnerUnbinder unbinder) {\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {\n"
        + "      @Override\n"
        + "      public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        target.doStuff(finder.<ListView>castParam(p0, \"onItemClick\", 0, \"doStuff\", 0)\n"
        + "        , p2);\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    View view1;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      this.target = target;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      T target = this.target;\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      ((AdapterView<?>) view1).setOnItemClickListener(null);\n"
        + "      view1 = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void onClickRootViewBinding() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.widget.ListView;\n"
        + "import butterknife.OnItemClick;\n"
        + "public class Test extends ListView {\n"
        + "  @OnItemClick void doStuff() {}\n"
        + "  public Test(Context context) {\n"
        + "    super(context);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    InnerUnbinder unbinder = new InnerUnbinder(target);\n"
        + "    bindToTarget(target, finder, source, unbinder);\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  protected static void bindToTarget(final Test target, Finder finder, Object source, InnerUnbinder unbinder) {\n"
        + "    View view;\n"
        + "    view = target;\n"
        + "    unbinder.viewOriginal = view;\n"
        + "    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {\n"
        + "      @Override\n"
        + "      public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    View viewOriginal;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      this.target = target;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      T target = this.target;\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      ((AdapterView<?>) viewOriginal).setOnItemClickListener(null);\n"
        + "      viewOriginal = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void failsWithInvalidId() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.content.Context;",
        "import android.app.Activity;",
        "import butterknife.OnItemClick;",
        "public class Test extends Activity {",
        "  @OnItemClick({1, -1}) void doStuff() {}",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@OnItemClick annotation contains invalid ID -1. (test.Test.doStuff)")
        .in(source).onLine(6);
  }

  @Test public void failsWithInvalidParameterConfiguration() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import android.widget.AdapterView;",
        "import butterknife.OnItemClick;",
        "public class Test extends Activity {",
        "  @OnItemClick(1) void doStuff(",
        "    AdapterView<?> parent,",
        "    View view,",
        "    View whatIsThis",
        "  ) {}",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(Joiner.on('\n').join(
            "Unable to match @OnItemClick method arguments. (test.Test.doStuff)",
            "  ",
            "    Parameter #1: android.widget.AdapterView<?>",
            "      matched listener parameter #1: android.widget.AdapterView<?>",
            "  ",
            "    Parameter #2: android.view.View",
            "      matched listener parameter #2: android.view.View",
            "  ",
            "    Parameter #3: android.view.View",
            "      did not match any listener parameters",
            "  ",
            "  Methods may have up to 4 parameter(s):",
            "  ",
            "    android.widget.AdapterView<?>",
            "    android.view.View",
            "    int",
            "    long",
            "  ",
            "  These may be listed in any order but will be searched for from top to bottom."))
        .in(source).onLine(7);
  }
}
