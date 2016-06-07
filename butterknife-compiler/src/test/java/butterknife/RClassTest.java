package butterknife;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import java.util.Arrays;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

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
        + "import butterknife.RClass;\n"
        + "@RClass(R.class)\n"
        + "public class Test extends Activity {\n"
        + "  @BindInt(R2.integer.res) int one;\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.content.res.Resources;\n"
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
        + "  protected static void bindToTarget(Test target, Resources res) {\n"
        + "    target.one = res.getInteger(test.R.integer.res);\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources()).that(Arrays.asList(source, NON_FINAL_R, R2))
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void app() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.BindBool;\n"
        + "import butterknife.RClass;\n"
        + "@RClass(R.class)\n"
        + "public class Test extends Activity {\n"
        + "  @BindBool(R.bool.res) boolean bool;\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.content.res.Resources;\n"
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
        + "  protected static void bindToTarget(Test target, Resources res) {\n"
        + "    target.bool = res.getBoolean(test.R.bool.res);\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources()).that(Arrays.asList(source, FINAL_R))
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void defineRMoreThanOnce(){
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.RClass;\n"
        + "@RClass(R.class)\n"
        + "public class Test {}\n"
    );

    JavaFileObject source2 = JavaFileObjects.forSourceString("test.Test2", ""
        + "package test;\n"
        + "import butterknife.RClass;\n"
        + "@RClass(R.class)\n"
        + "public class Test2 {}\n"
    );

    assertAbout(javaSources()).that(Arrays.asList(source, source2, NON_FINAL_R))
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("Can define @RClass only once");
  }

  @Test public void unknownResource() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.BindColor;\n"
        + "import butterknife.RClass;\n"
        + "@RClass(R.class)\n"
        + "public class Test extends Activity {\n"
        + "  @BindColor(android.R.color.black) int black;\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
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
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    bindToTarget(target, res, theme);\n"
        + "    return Unbinder.EMPTY;\n"
        + "  }\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  protected static void bindToTarget(Test target, Resources res, Resources.Theme theme) {\n"
        + "    target.black = Utils.getColor(res, theme, 17170444);\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources()).that(Arrays.asList(source, NON_FINAL_R))
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
