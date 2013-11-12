package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

public class OnClickTest {
  @Test public void onClickInjection() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(1) void doStuff() {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.Views.Finder;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source) {",
            "    View view;",
            "    view = finder.findById(source, 1);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '1' for method 'doStuff' was not found. If this method binding is optional add '@Optional'.\");",
            "    }",
            "    view.setOnClickListener(new View.OnClickListener() {",
            "      @Override public void onClick(View view) {",
            "        target.doStuff();",
            "      }",
            "    });",
            "  }",
            "  public static void reset(test.Test target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
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
        .processedWith(butterknifeProcessors())
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
        "  @OnClick(0) void click0() {}",
        "  @OnClick(1) void click1(View view) {}",
        "  @OnClick(2) void click2(TextView view) {}",
        "  @OnClick(3) void click3(Button button) {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.Views.Finder;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source) {",
            "    View view;",
            "    view = finder.findById(source, 0);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '0' for method 'click0' was not found. If this method binding is optional add '@Optional'.\");",
            "    }",
            "    view.setOnClickListener(new View.OnClickListener() {",
            "      @Override public void onClick(View view) {",
            "        target.click0();",
            "      }",
            "    });",
            "    view = finder.findById(source, 1);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '1' for method 'click1' was not found. If this method binding is optional add '@Optional'.\");",
            "    }",
            "    view.setOnClickListener(new View.OnClickListener() {",
            "      @Override public void onClick(View view) {",
            "        target.click1((android.view.View) view);",
            "      }",
            "    });",
            "    view = finder.findById(source, 2);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '2' for method 'click2' was not found. If this method binding is optional add '@Optional'.\");",
            "    }",
            "    view.setOnClickListener(new View.OnClickListener() {",
            "      @Override public void onClick(View view) {",
            "        target.click2((android.widget.TextView) view);",
            "      }",
            "    });",
            "    view = finder.findById(source, 3);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '3' for method 'click3' was not found. If this method binding is optional add '@Optional'.\");",
            "    }",
            "    view.setOnClickListener(new View.OnClickListener() {",
            "      @Override public void onClick(View view) {",
            "        target.click3((android.widget.Button) view);",
            "      }",
            "    });",
            "  }",
            "  public static void reset(test.Test target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
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

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.Views.Finder;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source) {",
            "    View view;",
            "    view = finder.findById(source, 1);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '1' for method 'click' was not found. If this method binding is optional add '@Optional'.\");",
            "    }",
            "    view.setOnClickListener(new View.OnClickListener() {",
            "      @Override public void onClick(View view) {",
            "        target.click();",
            "      }",
            "    });",
            "    view = finder.findById(source, 2);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '2' for method 'click' was not found. If this method binding is optional add '@Optional'.\");",
            "    }",
            "    view.setOnClickListener(new View.OnClickListener() {",
            "      @Override public void onClick(View view) {",
            "        target.click();",
            "      }",
            "    });",
            "    view = finder.findById(source, 3);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '3' for method 'click' was not found. If this method binding is optional add '@Optional'.\");",
            "    }",
            "    view.setOnClickListener(new View.OnClickListener() {",
            "      @Override public void onClick(View view) {",
            "        target.click();",
            "      }",
            "    });",
            "  }",
            "  public static void reset(test.Test target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void optional() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "import butterknife.Optional;",
        "public class Test extends Activity {",
        "  @Optional @OnClick(1) void doStuff() {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.Views.Finder;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source) {",
            "    View view;",
            "    view = finder.findById(source, 1);",
            "    if (view != null) {",
            "      view.setOnClickListener(new View.OnClickListener() {",
            "        @Override public void onClick(View view) {",
            "          target.doStuff();",
            "        }",
            "      });",
            "    }",
            "  }",
            "  public static void reset(test.Test target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void onClickInjectionFailsIfHasReturnType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.app.Activity;",
            "import butterknife.OnClick;",
            "import java.lang.String;",
            "public class Test extends Activity {",
            "  @OnClick(1)",
            "  public String doStuff() {",
            "  }",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            String.format("@OnClick methods must have a 'void' return type (%s).",
                "test.Test.doStuff()"))
        .in(source).onLine(7);
  }

  @Test public void onClickInjectionFailsIfPrivate() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.app.Activity;",
            "import butterknife.OnClick;",
            "public class Test extends Activity {",
            "  @OnClick(1)",
            "  private void doStuff() {",
            "  }",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            String.format("@OnClick methods must not be private or static (%s).",
                "test.Test.doStuff()"))
        .in(source).onLine(6);
  }

  @Test public void onClickInjectionFailsIfStatic() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.app.Activity;",
            "import butterknife.OnClick;",
            "public class Test extends Activity {",
            "  @OnClick(1)",
            "  public static void doStuff() {",
            "  }",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            String.format("@OnClick methods must not be private or static (%s).",
                "test.Test.doStuff()"))
        .in(source).onLine(6);
  }

  @Test public void onClickInjectionFailsIfParameterNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.app.Activity;",
            "import butterknife.OnClick;",
            "public class Test extends Activity {",
            "  @OnClick(1)",
            "  public void doStuff(String thing) {",
            "  }",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            String.format("@OnClick method parameter must extend from View (%s).",
                "test.Test.doStuff(java.lang.String)"))
        .in(source).onLine(6);
  }

  @Test public void onClickInjectionFailsIfInInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import butterknife.OnClick;",
            "public interface Test {",
            "  @OnClick(1)",
            "  void doStuff();",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            String.format("@OnClick method annotations may only be specified in classes" +
                " (%s).",
                "test.Test"))
        .in(source).onLine(5);
  }

  @Test public void onClickInjectionFailsIfHasDuplicateIds() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.app.Activity;",
            "import butterknife.OnClick;",
            "public class Test extends Activity {",
            "  @OnClick({1, 2, 3, 1})",
            "  void doStuff() {",
            "  }",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            String.format("@OnClick annotation for method %s contains duplicate ID %d.",
                "doStuff()", 1))
        .in(source).onLine(6);
  }
}
