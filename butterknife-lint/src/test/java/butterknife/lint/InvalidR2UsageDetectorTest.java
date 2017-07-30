package butterknife.lint;

import com.android.tools.lint.checks.infrastructure.TestFile;
import org.junit.Test;

import static com.android.tools.lint.checks.infrastructure.TestFiles.java;
import static com.android.tools.lint.checks.infrastructure.TestLintTask.lint;

public final class InvalidR2UsageDetectorTest {
  private static final TestFile BIND_TEST = java(""
      + "package sample.r2;\n"
      + "\n"
      + "import java.lang.annotation.ElementType;\n"
      + "import java.lang.annotation.Retention;\n"
      + "import java.lang.annotation.RetentionPolicy;\n"
      + "import java.lang.annotation.Target;\n"
      + "\n"
      + "@Retention(RetentionPolicy.SOURCE) @Target({ ElementType.FIELD, ElementType.METHOD })\n"
      + "public @interface BindTest {\n"
      + "  int value();\n"
      + "}\n");

  private static final TestFile R2 = java(""
      + "package sample.r2;\n"
      + "\n"
      + "public final class R2 {\n"
      + "  public static final class array {\n"
      + "    public static final int res = 0x7f040001;\n"
      + "  }\n"
      + "\n"
      + "  public static final class attr {\n"
      + "    public static final int res = 0x7f040002;\n"
      + "  }\n"
      + "\n"
      + "  public static final class bool {\n"
      + "    public static final int res = 0x7f040003;\n"
      + "  }\n"
      + "\n"
      + "  public static final class color {\n"
      + "    public static final int res = 0x7f040004;\n"
      + "  }\n"
      + "\n"
      + "  public static final class dimen {\n"
      + "    public static final int res = 0x7f040005;\n"
      + "  }\n"
      + "\n"
      + "  public static final class drawable {\n"
      + "    public static final int res = 0x7f040006;\n"
      + "  }\n"
      + "\n"
      + "  public static final class id {\n"
      + "    public static final int res = 0x7f040007;\n"
      + "  }\n"
      + "\n"
      + "  public static final class integer {\n"
      + "    public static final int res = 0x7f040008;\n"
      + "  }\n"
      + "\n"
      + "  public static final class string {\n"
      + "    public static final int res = 0x7f040009;\n"
      + "  }\n"
      + "}");

  @Test public void noR2Usage() {
    lint() //
        .files(R2, //
            java("" //
                + "package sample;\n" //
                + "class NoR2Usage {}\n")) //
        .issues(InvalidR2UsageDetector.ISSUE) //
        .run() //
        .expectClean();
  }

  @Test public void usesR2InAnnotations() {
    lint() //
        .files(R2, BIND_TEST, //
            java(""
                + "package sample.r2;\n"
                + "\n"
                + "public class R2UsageInAnnotations {\n"
                + "\n"
                + "  @BindTest(sample.r2.R2.string.res) String test;\n"
                + "\n"
                + "  @BindTest(R2.id.res) public void foo() {}\n"
                + "}\n") //
        ) //
        .issues(InvalidR2UsageDetector.ISSUE) //
        .run() //
        .expectClean();
  }

  @Test public void usesR2OutsideAnnotations() {
    lint() //
        .files(R2, //
            java(""
                + "package sample.r2;\n"
                + "\n"
                + "public class R2UsageOutsideAnnotations {\n"
                + "\n"
                + "  int array = sample.r2.R2.array.res;\n"
                + "\n"
                + "  public void foo(int color) {}\n"
                + "\n"
                + "  public void bar() {\n"
                + "    foo(R2.color.res);\n"
                + "  }\n"
                + "}\n" //
            )) //
        .issues(InvalidR2UsageDetector.ISSUE) //
        .run() //
        .expectErrorCount(2) //
        .expectWarningCount(0);
  }

  @Test public void usesR2WithSuppression() {
    lint() //
        .files(R2, java(""
            + "package sample.r2;\n"
            + "\n"
            + "public class R2UsageWithSuppression {\n"
            + "\n"
            + "  @SuppressWarnings(\"InvalidR2Usage\")\n"
            + "  int bool = sample.r2.R2.bool.res;\n"
            + "\n"
            + "  public void foo(int attr) {}\n"
            + "\n"
            + "  @SuppressWarnings(\"InvalidR2Usage\")\n"
            + "  public void bar() {\n"
            + "    foo(R2.attr.res);\n"
            + "  }\n"
            + "}\n")) //
        .issues(InvalidR2UsageDetector.ISSUE) //
        .run() //
        .expectClean();
  }
}
