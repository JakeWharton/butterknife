package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindStringTest {
  @Test public void simple() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.BindString;\n"
        + "public class Test extends Activity {\n"
        + "  @BindString(1) String one;\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public final class Test_ViewBinder implements ViewBinder<Test> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, Test target, Object source) {\n"
        + "    Resources res = finder.getContext(source).getResources();\n"
        + "    bindToTarget(target, res);\n"
        + "    return Unbinder.EMPTY;\n"
        + "  }\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public static void bindToTarget(Test target, Resources res) {\n"
        + "    target.one = res.getString(1);\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void finalClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.BindString;\n"
        + "public final class Test extends Activity {\n"
        + "  @BindString(1) String one;\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import android.content.res.Resources;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public final class Test_ViewBinder implements ViewBinder<Test> {\n"
        + "  @Override\n"
        + "  @SuppressWarnings(\"ResourceType\")"
        + "  public Unbinder bind(Finder finder, Test target, Object source) {\n"
        + "    Resources res = finder.getContext(source).getResources();\n"
        + "    target.one = res.getString(1);\n"
        + "    return Unbinder.EMPTY;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void typeMustBeString() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.BindString;\n"
        + "public class Test extends Activity {\n"
        + "  @BindString(1) boolean one;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindString field type must be 'String'. (test.Test.one)")
        .in(source).onLine(5);
  }
}
