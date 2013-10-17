package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

public class InjectViewTest {

  @Test
  public void injectingView() {
    final JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.app.Activity;",
            "import android.view.View;",
            "import butterknife.InjectView;",
            "public class Test extends Activity {",
            "    @InjectView(1) View thing;",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError();
    // TODO: confirm generated source (should expect test.Test$$ViewInjector.java)
  }

  @Test
  public void injectingViewFailsIfInPrivateClass() {
    final JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.InjectView;",
            "public class Test {",
            "  private static class Inner {",
            "    @InjectView(1) View thing;",
            "  }",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            String.format("@InjectView fields may not be on private classes (%s).",
                "test.Test.Inner"))
        .in(source).onLine(6);
  }

  @Test
  public void injectViewFailsIfNotView() {
    final JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.app.Activity;",
            "import butterknife.InjectView;",
            "public class Test extends Activity {",
            "  @InjectView(1) String thing;",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            String.format("@InjectView fields must extend from View (%s).",
                "test.Test.thing"))
        .in(source).onLine(5);
  }

  @Test
  public void injectViewFailsIfInInterface() {
    final JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.InjectView;",
            "public interface Test {",
            "    @InjectView(1) View thing = null;",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            String.format("@InjectView field annotations may only be specified in " +
                "classes (%s).",
                "test.Test"))
        .in(source).onLine(5);
  }

  @Test
  public void injectingViewFailsIfPrivate() {
    final JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.app.Activity;",
            "import android.view.View;",
            "import butterknife.InjectView;",
            "public class Test extends Activity {",
            "    @InjectView(1) private View thing;",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            String.format("@InjectView fields must not be private or static (%s).",
                "test.Test.thing"))
        .in(source).onLine(6);
  }

  @Test
  public void injectingViewFailsIfStatic() {
    final JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.app.Activity;",
            "import android.view.View;",
            "import butterknife.InjectView;",
            "public class Test extends Activity {",
            "    @InjectView(1) static View thing;",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            String.format("@InjectView fields must not be private or static (%s).",
                "test.Test.thing"))
        .in(source).onLine(6);
  }
}
