package butterknife;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindArrayTest {
  @Test public void stringArray() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.BindArray;",
        "public class Test extends Activity {",
        "  @BindArray(1) String[] one;",
        "}"
    ));

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
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    Resources res = finder.getContext(source).getResources();\n"
        + "    target.one = res.getStringArray(1);\n"
        + "    return Unbinder.EMPTY;\n"
        + "  }\n"
        + "}");

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void intArray() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.BindArray;",
        "public class Test extends Activity {",
        "  @BindArray(1) int[] one;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.res.Resources;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.ViewBinder;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "import java.lang.SuppressWarnings;",
            "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
            "  @Override",
            "  @SuppressWarnings(\"ResourceType\")",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    Resources res = finder.getContext(source).getResources();",
            "    target.one = res.getIntArray(1);",
            "    return Unbinder.EMPTY",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void textArray() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.BindArray;",
        "public class Test extends Activity {",
        "  @BindArray(1) CharSequence[] one;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.res.Resources;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.ViewBinder;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "import java.lang.SuppressWarnings;",
            "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
            "  @Override",
            "  @SuppressWarnings(\"ResourceType\")",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    Resources res = finder.getContext(source).getResources();",
            "    target.one = res.getTextArray(1);",
            "    return Unbinder.EMPTY",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void typedArray() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.BindArray;",
        "import android.content.res.TypedArray;",
        "public class Test extends Activity {",
        "  @BindArray(1) TypedArray one;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.res.Resources;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.ViewBinder;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "import java.lang.SuppressWarnings;",
            "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
            "  @Override",
            "  @SuppressWarnings(\"ResourceType\")",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    Resources res = finder.getContext(source).getResources();",
            "    target.one = res.obtainTypedArray(1);",
            "    return Unbinder.EMPTY",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void typeMustBeSupported() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.BindArray;",
        "public class Test extends Activity {",
        "  @BindArray(1) String one;",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindArray field type must be one of: String[], int[], CharSequence[], "
                + "android.content.res.TypedArray. (test.Test.one)")
        .in(source).onLine(5);
  }
}
