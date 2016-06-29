package butterknife;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindStringsTest {
  @Test
  public void listOfStringResources() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import android.app.Activity;\n"
            + "import java.util.List;\n"
            + "import butterknife.BindStrings;\n"
            + "public class Test extends Activity {\n"
            + "  @BindStrings({1, 2}) List<String> strings;\n"
            + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
            + "package test;\n\n"
            + "import android.content.res.Resources;\n"
            + "import butterknife.Unbinder;\n"
            + "import butterknife.internal.Finder;\n"
            + "import butterknife.internal.Utils;\n"
            + "import butterknife.internal.ViewBinder;\n"
            + "import java.lang.Object;\n"
            + "import java.lang.Override;\n\n"
            + "public final class Test_ViewBinder implements ViewBinder<Test> {\n"
            + "  @Override\n"
            + "  public Unbinder bind(Finder finder, Test target, Object source) {\n"
            + "    Resources res = finder.getContext(source).getResources();\n"
            + "    bindToTarget(target, res);\n"
            + "    return Unbinder.EMPTY;\n"
            + "  }\n\n"
            + "  public static void bindToTarget(Test target, Resources res) {\n"
            + "    target.strings = Utils.listOf(\n" +
            "        res.getString(1), \n" +
            "        res.getString(2));\n"
            + "  }\n"
            + "}"
    );

    assertAbout(javaSource()).that(source)
            .processedWith(new ButterKnifeProcessor())
            .compilesWithoutError()
            .and()
            .generatesSources(expectedSource);
  }

  @Test
  public void arrayOfStringResources() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import android.app.Activity;\n"
            + "import java.util.List;\n"
            + "import butterknife.BindStrings;\n"
            + "public class Test extends Activity {\n"
            + "  @BindStrings({1, 2}) String[] strings;\n"
            + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
            + "package test;\n\n"
            + "import android.content.res.Resources;\n"
            + "import butterknife.Unbinder;\n"
            + "import butterknife.internal.Finder;\n"
            + "import butterknife.internal.Utils;\n"
            + "import butterknife.internal.ViewBinder;\n"
            + "import java.lang.Object;\n"
            + "import java.lang.Override;\n\n"
            + "public final class Test_ViewBinder implements ViewBinder<Test> {\n"
            + "  @Override\n"
            + "  public Unbinder bind(Finder finder, Test target, Object source) {\n"
            + "    Resources res = finder.getContext(source).getResources();\n"
            + "    bindToTarget(target, res);\n"
            + "    return Unbinder.EMPTY;\n"
            + "  }\n\n"
            + "  public static void bindToTarget(Test target, Resources res) {\n"
            + "    target.strings = Utils.arrayOf(\n" +
            "        res.getString(1), \n" +
            "        res.getString(2));\n"
            + "  }\n"
            + "}"
    );

    assertAbout(javaSource()).that(source)
            .processedWith(new ButterKnifeProcessor())
            .compilesWithoutError()
            .and()
            .generatesSources(expectedSource);
  }

  @Test public void typeMustBeAListOrAnArray() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import android.app.Activity;\n"
            + "import butterknife.BindStrings;\n"
            + "public class Test extends Activity {\n"
            + "  @BindStrings({1, 2}) String strings;\n"
            + "}"
    );

    assertAbout(javaSource()).that(source)
            .processedWith(new ButterKnifeProcessor())
            .failsToCompile()
            .withErrorContaining("@BindStrings must be used with a List or an array. (test.Test.strings)")
            .in(source).onLine(5);
  }

  @Test public void ListTypeHasToBeString() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import android.app.Activity;\n"
            + "import java.util.List;\n"
            + "import butterknife.BindStrings;\n"
            + "public class Test extends Activity {\n"
            + "  @BindStrings({1, 2}) List<Integer> strings;\n"
            + "}"
    );

    assertAbout(javaSource()).that(source)
            .processedWith(new ButterKnifeProcessor())
            .failsToCompile()
            .withErrorContaining("@BindStrings List or array type must be String. (test.Test.strings)")
            .in(source).onLine(6);
  }

}
