package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindBitmapTest {
  @Test public void simple() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import android.graphics.Bitmap;\n"
        + "import butterknife.BindBitmap;\n"
        + "public class Test extends Activity {\n"
        + "  @BindBitmap(1) Bitmap one;\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.content.res.Resources;\n"
        + "import android.graphics.BitmapFactory;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    Resources res = finder.getContext(source).getResources();\n"
        + "    bindToTarget(target, res);\n"
        + "    return Unbinder.EMPTY;\n"
        + "  }\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  protected static void bindToTarget(final Test target, Resources res) {\n"
        + "    target.one = BitmapFactory.decodeResource(res, 1);\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void typeMustBeBitmap() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.BindBitmap;\n"
        + "public class Test extends Activity {\n"
        + "  @BindBitmap(1) String one;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindBitmap field type must be 'Bitmap'. (test.Test.one)")
        .in(source).onLine(5);
  }
}
