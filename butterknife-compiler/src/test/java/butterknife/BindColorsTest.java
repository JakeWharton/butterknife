package butterknife;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindColorsTest {
  @Test
  public void simpleIntegerList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import android.app.Activity;\n"
            + "import java.util.List;\n"
            + "import butterknife.BindColors;\n"
            + "public class Test extends Activity {\n"
            + "  @BindColors({1, 2}) List<Integer> colors;\n"
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
            + "public final class Test_ViewBinder implements ViewBinder<Test> {\n"
            + "  @Override\n"
            + "  public Unbinder bind(Finder finder, Test target, Object source) {\n"
            + "    Context context = finder.getContext(source);\n"
            + "    Resources res = context.getResources();\n"
            + "    Resources.Theme theme = context.getTheme();\n"
            + "    bindToTarget(target, res, theme);\n"
            + "    return Unbinder.EMPTY;\n"
            + "  }\n\n"
            + "  public static void bindToTarget(Test target, Resources res, Resources.Theme theme) {\n"
            + "    target.colors = Utils.listOf(\n"
            + "        Utils.getColor(res, theme, 1),\n"
            + "        Utils.getColor(res, theme, 2));\n"
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
  public void simpleIntegerArray() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import android.app.Activity;\n"
            + "import butterknife.BindColors;\n"
            + "public class Test extends Activity {\n"
            + "  @BindColors({1, 2}) Integer[] colors;\n"
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
            + "public final class Test_ViewBinder implements ViewBinder<Test> {\n"
            + "  @Override\n"
            + "  public Unbinder bind(Finder finder, Test target, Object source) {\n"
            + "    Context context = finder.getContext(source);\n"
            + "    Resources res = context.getResources();\n"
            + "    Resources.Theme theme = context.getTheme();\n"
            + "    bindToTarget(target, res, theme);\n"
            + "    return Unbinder.EMPTY;\n"
            + "  }\n\n"
            + "  public static void bindToTarget(Test target, Resources res, Resources.Theme theme) {\n"
            + "    target.colors = Utils.arrayOf(\n"
            + "        Utils.getColor(res, theme, 1),\n"
            + "        Utils.getColor(res, theme, 2));\n"
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
  public void simpleColorStateListList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import android.app.Activity;\n"
            + "import android.content.res.ColorStateList;\n"
            + "import java.util.List;\n"
            + "import butterknife.BindColors;\n"
            + "public class Test extends Activity {\n"
            + "  @BindColors({1, 2}) List<ColorStateList> colors;\n"
            +"}"
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
            + "public final class Test_ViewBinder implements ViewBinder<Test> {\n"
            + "  @Override\n"
            + "  public Unbinder bind(Finder finder, Test target, Object source) {\n"
            + "    Context context = finder.getContext(source);\n"
            + "    Resources res = context.getResources();\n"
            + "    Resources.Theme theme = context.getTheme();\n"
            + "    bindToTarget(target, res, theme);\n"
            + "    return Unbinder.EMPTY;\n"
            + "  }\n\n"
            + "  public static void bindToTarget(Test target, Resources res, Resources.Theme theme) {\n"
            + "    target.colors = Utils.listOf(\n" +
            "        Utils.getColorStateList(res, theme, 1), \n" +
            "        Utils.getColorStateList(res, theme, 2));"
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
  public void simpleColorStateListArray() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import android.app.Activity;\n"
            + "import android.content.res.ColorStateList;\n"
            + "import butterknife.BindColors;\n"
            + "public class Test extends Activity {\n"
            + "  @BindColors({1, 2}) ColorStateList[] colors;\n"
            +"}"
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
            + "public final class Test_ViewBinder implements ViewBinder<Test> {\n"
            + "  @Override\n"
            + "  public Unbinder bind(Finder finder, Test target, Object source) {\n"
            + "    Context context = finder.getContext(source);\n"
            + "    Resources res = context.getResources();\n"
            + "    Resources.Theme theme = context.getTheme();\n"
            + "    bindToTarget(target, res, theme);\n"
            + "    return Unbinder.EMPTY;\n"
            + "  }\n\n"
            + "  public static void bindToTarget(Test target, Resources res, Resources.Theme theme) {\n"
            + "    target.colors = Utils.arrayOf(\n" +
            "        Utils.getColorStateList(res, theme, 1), \n" +
            "        Utils.getColorStateList(res, theme, 2));"
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
            + "import butterknife.BindColors;\n"
            + "public class Test extends Activity {\n"
            + "  @BindColors({1, 2}) String colors;\n"
            + "}"
    );

    assertAbout(javaSource()).that(source)
            .processedWith(new ButterKnifeProcessor())
            .failsToCompile()
            .withErrorContaining("@BindColors must be used with a List or an array. (test.Test.colors)")
            .in(source).onLine(5);
  }

  @Test public void ListTypeHasToBeIntegerOrColorStateList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import android.app.Activity;\n"
            + "import java.util.List;\n"
            + "import butterknife.BindColors;\n"
            + "public class Test extends Activity {\n"
            + "  @BindColors({1, 2}) List<String> colors;\n"
            + "}"
    );

    assertAbout(javaSource()).that(source)
            .processedWith(new ButterKnifeProcessor())
            .failsToCompile()
            .withErrorContaining("@BindColors List or array type must be Integer or ColorStateList. (test.Test.colors)")
            .in(source).onLine(6);
  }

}
