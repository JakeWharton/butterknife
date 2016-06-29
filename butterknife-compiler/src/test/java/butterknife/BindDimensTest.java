package butterknife;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Ignore;
import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindDimensTest {

  @Test public void simpleIntegerList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import android.app.Activity;\n"
            + "import java.util.List;\n"
            + "import butterknife.BindDimens;\n"
            + "public class Test extends Activity {\n"
            + "  @BindDimens({1, 2}) List<Integer> dimens;\n"
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
            + "    target.dimens = Utils.listOf(\n"
            + "        res.getDimensionPixelSize(1),\n"
            + "        res.getDimensionPixelSize(2));\n"
            + "  }\n"
            + "}"
    );

    assertAbout(javaSource()).that(source)
            .processedWith(new ButterKnifeProcessor())
            .compilesWithoutError()
            .and()
            .generatesSources(expectedSource);
  }

  @Test public void simpleIntegerArray() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import android.app.Activity;\n"
            + "import java.util.List;\n"
            + "import butterknife.BindDimens;\n"
            + "public class Test extends Activity {\n"
            + "  @BindDimens({1, 2}) Integer[] dimens;\n"
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
            + "    target.dimens = Utils.arrayOf(\n"
            + "        res.getDimensionPixelSize(1),\n"
            + "        res.getDimensionPixelSize(2));\n"
            + "  }\n"
            + "}"
    );

    assertAbout(javaSource()).that(source)
            .processedWith(new ButterKnifeProcessor())
            .compilesWithoutError()
            .and()
            .generatesSources(expectedSource);
  }

  @Test public void simpleFloatList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import android.app.Activity;\n"
            + "import java.util.List;\n"
            + "import butterknife.BindDimens;\n"
            + "public class Test extends Activity {\n"
            + "  @BindDimens({1, 2}) List<Float> dimens;\n"
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
            + "    target.dimens = Utils.listOf(\n"
            + "        res.getDimension(1),\n"
            + "        res.getDimension(2));\n"
            + "  }\n"
            + "}"
    );

    assertAbout(javaSource()).that(source)
            .processedWith(new ButterKnifeProcessor())
            .compilesWithoutError()
            .and()
            .generatesSources(expectedSource);

  }

  @Test public void simpleFloatArray() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import android.app.Activity;\n"
            + "import java.util.List;\n"
            + "import butterknife.BindDimens;\n"
            + "public class Test extends Activity {\n"
            + "  @BindDimens({1, 2}) Float[] dimens;\n"
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
            + "    target.dimens = Utils.arrayOf(\n"
            + "        res.getDimension(1),\n"
            + "        res.getDimension(2));\n"
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
            + "import butterknife.BindDimens;\n"
            + "public class Test extends Activity {\n"
            + "  @BindDimens({1, 2}) String one;\n"
            + "}"
    );

    assertAbout(javaSource()).that(source)
            .processedWith(new ButterKnifeProcessor())
            .failsToCompile()
            .withErrorContaining("@BindDimens must be used with a List or an array. (test.Test.one)")
            .in(source).onLine(5);
  }

  @Test public void ListTypeHasToBeIntegerOrFloat() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import android.app.Activity;\n"
            + "import java.util.List;\n"
            + "import butterknife.BindDimens;\n"
            + "public class Test extends Activity {\n"
            + "  @BindDimens({1, 2}) List<String> one;\n"
            + "}"
    );

    assertAbout(javaSource()).that(source)
            .processedWith(new ButterKnifeProcessor())
            .failsToCompile()
            .withErrorContaining("@BindDimens List or array type must be Integer or Float. (test.Test.one)")
            .in(source).onLine(6);
  }


}
