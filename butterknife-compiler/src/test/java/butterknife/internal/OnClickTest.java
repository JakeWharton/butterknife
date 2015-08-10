package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.ASSERT;
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

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife;",
            "import butterknife.internal.DebouncingOnClickListener;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterKnife.ViewBinder<T> {",
            "  @Override public void bind(final ButterKnife.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.doStuff();",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
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

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife;",
            "import butterknife.internal.DebouncingOnClickListener;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterKnife.ViewBinder<T> {",
            "  @Override public void bind(final ButterKnife.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff1', method 'doStuff2', and method 'doStuff3'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.doStuff1();",
            "        target.doStuff2();",
            "        target.doStuff3(p0);",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, 2, \"method 'doStuff3'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.doStuff3(p0);",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
            "}"));

    ASSERT.about(javaSource()).that(source)
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

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife;",
            "import butterknife.internal.DebouncingOnClickListener;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterKnife.ViewBinder<T> {",
            "  @Override public void bind(final ButterKnife.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"field 'view' and method 'doStuff'\");",
            "    target.view = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.doStuff();",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.view = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
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

    ASSERT.about(javaSource()).that(source)
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

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.Button;",
            "import android.widget.TextView;",
            "import butterknife.ButterKnife;",
            "import butterknife.internal.DebouncingOnClickListener;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterKnife.ViewBinder<T> {",
            "  @Override public void bind(final ButterKnife.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 0, \"method 'click0'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.click0();",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, 1, \"method 'click1'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.click1(p0);",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, 2, \"method 'click2'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.click2(finder.<TextView>castParam(p0, \"doClick\", 0, \"click2\", 0));",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, 3, \"method 'click3'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.click3(finder.<Button>castParam(p0, \"doClick\", 0, \"click3\", 0);",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, 4, \"method 'click4'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.click4(finder.<Test.TestInterface>castParam(p0, \"doClick\", 0, \"click4\", 0);",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
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

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife;",
            "import butterknife.internal.DebouncingOnClickListener;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterKnife.ViewBinder<T> {",
            "  @Override public void bind(final ButterKnife.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'click'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.click();",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, 2, \"method 'click'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.click();",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, 3, \"method 'click'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.click();",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void optional() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @interface Nullable {}",
        "  @Nullable @OnClick(1) void doStuff() {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife;",
            "import butterknife.internal.DebouncingOnClickListener;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterKnife.ViewBinder<T> {",
            "  @Override public void bind(final ButterKnife.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findOptionalView(source, 1, null);",
            "    if (view != null) {",
            "      view.setOnClickListener(new DebouncingOnClickListener() {",
            "        @Override public void doClick(View p0) {",
            "          target.doStuff();",
            "        }",
            "      });",
            "    }",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
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
        "public class Test extends Activity {",
        "  @interface Nullable {}",
        "  @Bind(1) View view;",
        "  @Nullable @OnClick(1) void doStuff() {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife;",
            "import butterknife.internal.DebouncingOnClickListener;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterKnife.ViewBinder<T> {",
            "  @Override public void bind(final ButterKnife.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"field 'view'\");",
            "    target.view = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.doStuff();",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.view = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
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

    ASSERT.about(javaSource()).that(source)
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

    ASSERT.about(javaSource()).that(source)
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

    ASSERT.about(javaSource()).that(source)
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

    ASSERT.about(javaSource()).that(source)
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

    ASSERT.about(javaSource()).that(source)
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

    ASSERT.about(javaSource()).that(source)
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

    ASSERT.about(javaSource()).that(source)
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

    ASSERT.about(javaSource()).that(source)
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

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick annotation for method contains duplicate ID 1. (test.Test.doStuff)")
        .in(source).onLine(6);
  }
}
