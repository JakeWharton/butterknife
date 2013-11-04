package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

public class OnClickTest {

  @Test
  public void onClickInjection() {
    // NOTE: needs to be in non-default package, otherwise got ".est$$ViewInjector.java"
    final JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(1)",
        "  void doStuff() {",
        "  }",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError();
    // TODO: confirm generated source (should expect test.Test$$ViewInjector.java)
  }

  @Test
  public void onClickInjectionFailsIfHasReturnType() {
    final JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
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

  @Test
  public void onClickInjectionFailsIfPrivate() {
    final JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
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

  @Test
  public void onClickInjectionFailsIfStatic() {
    final JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
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

  @Test
  public void onClickInjectionFailsIfParameterNotView() {
    final JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
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

  @Test
  public void onClickInjectionFailsIfInInterface() {
    final JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
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

  @Test
  public void onClickInjectionFailsIfHasDuplicateIds() {
    final JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
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
