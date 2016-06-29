package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindDrawableTest {
  @Test public void simple() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import android.graphics.drawable.Drawable;\n"
        + "import butterknife.BindDrawable;\n"
        + "public class Test extends Activity {\n"
        + "  @BindDrawable(1) Drawable one;\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
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
        + "public final class Test_ViewBinder implements ViewBinder<Test> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, Test target, Object source) {\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    bindToTarget(target, res, theme);\n"
        + "    return Unbinder.EMPTY;\n"
        + "  }\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public static void bindToTarget(Test target, Resources res, Resources.Theme theme) {\n"
        + "    target.one = Utils.getDrawable(res, theme, 1);\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void withTint() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import android.graphics.drawable.Drawable;\n"
        + "import butterknife.BindDrawable;\n"
        + "public class Test extends Activity {\n"
        + "  @BindDrawable(value = 1, tint = 2) Drawable one;\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
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
        + "public final class Test_ViewBinder implements ViewBinder<Test> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, Test target, Object source) {\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    bindToTarget(target, res, theme);\n"
        + "    return Unbinder.EMPTY;\n"
        + "  }\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public static void bindToTarget(Test target, Resources res, Resources.Theme theme) {\n"
        + "    target.one = Utils.getTintedDrawable(res, theme, 1, 2);\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void typeMustBeDrawable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.BindDrawable;\n"
        + "public class Test extends Activity {\n"
        + "  @BindDrawable(1) String one;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindDrawable field type must be 'Drawable'. (test.Test.one)")
        .in(source).onLine(5);
  }
}
