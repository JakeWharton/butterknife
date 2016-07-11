package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class RClassTest {

  private static final JavaFileObject NON_FINAL_R = JavaFileObjects.forSourceString("test.R", ""
      + "package test;\n"
      + "public final class R {\n"
      + "  public static final class array {\n"
      + "    public static int res = 0x7f040001;\n"
      + "  }\n"
      + "  public static final class bool {\n"
      + "    public static int res = 0x7f040002;\n"
      + "  }\n"
      + "  public static final class color {\n"
      + "    public static int res = 0x7f040003;\n"
      + "  }\n"
      + "  public static final class integer {\n"
      + "    public static int res = 0x7f040004;\n"
      + "  }\n"
      + "  public static final class styleable {\n"
      + "    public static int[] ActionBar = { 0x7f010001, 0x7f010003 };\n"
      + "  }\n"
      + "}"
  );

  private static final JavaFileObject FINAL_R = JavaFileObjects.forSourceString("test.R", ""
      + "package test;\n"
      + "public final class R {\n"
      + "  public static final class array {\n"
      + "    public static final int res = 0x7f040001;\n"
      + "  }\n"
      + "  public static final class bool {\n"
      + "    public static final int res = 0x7f040002;\n"
      + "  }\n"
      + "  public static final class color {\n"
      + "    public static final int res = 0x7f040003;\n"
      + "  }\n"
      + "  public static final class integer {\n"
      + "    public static final int res = 0x7f040004;\n"
      + "  }\n"
      + "  public static final class string {\n"
      + "    public static final int res = 0x7f040005;\n"
      + "  }\n"
      + "  public static final class styleable {\n"
      + "    public static final int[] ActionBar = { 0x7f010001, 0x7f010003 };\n"
      + "  }\n"
      + "}"
  );

  private static final JavaFileObject R2 = JavaFileObjects.forSourceString("test.R2", ""
      + "package test;\n"
      + "public final class R2 {\n"
      + "  public static final class array {\n"
      + "    public static final int res = 0x7f040001;\n"
      + "  }\n"
      + "  public static final class bool {\n"
      + "    public static final int res = 0x7f040002;\n"
      + "  }\n"
      + "  public static final class color {\n"
      + "    public static final int res = 0x7f040003;\n"
      + "  }\n"
      + "  public static final class integer {\n"
      + "    public static final int res = 0x7f040004;\n"
      + "  }\n"
      + "  public static final class string {\n"
      + "    public static final int res = 0x7f040005;\n"
      + "  }\n"
      + "}"
  );

  @Test public void library() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.BindInt;\n"
        + "public class Test extends Activity {\n"
        + "  @BindInt(R2.integer.res) int one;\n"
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
        + "public final class Test_ViewBinder implements ViewBinder<Test> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, Test target, Object source) {\n"
        + "    Resources res = finder.getContext(source).getResources();\n"
        + "    bindToTarget(target, res);\n"
        + "    return Unbinder.EMPTY;\n"
        + "  }\n"
        + "  public static void bindToTarget(Test target, Resources res) {\n"
        + "    target.one = res.getInteger(R.integer.res);\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources()).that(asList(source, NON_FINAL_R, R2))
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void app() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.BindBool;\n"
        + "public class Test extends Activity {\n"
        + "  @BindBool(R.bool.res) boolean bool;\n"
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
        + "public final class Test_ViewBinder implements ViewBinder<Test> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, Test target, Object source) {\n"
        + "    Resources res = finder.getContext(source).getResources();\n"
        + "    bindToTarget(target, res);\n"
        + "    return Unbinder.EMPTY;\n"
        + "  }\n"
        + "  public static void bindToTarget(Test target, Resources res) {\n"
        + "    target.bool = res.getBoolean(R.bool.res);\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources()).that(asList(source, FINAL_R))
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void compiledRClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.BindColor;\n"
        + "public class Test extends Activity {\n"
        + "  @BindColor(android.R.color.black) int black;\n"
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
        + "  }\n"
        + "  public static void bindToTarget(Test target, Resources res, Resources.Theme theme) {\n"
        + "    target.black = Utils.getColor(res, theme, android.R.color.black);\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources()).that(asList(source, NON_FINAL_R))
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(expectedSource);
  }
}
