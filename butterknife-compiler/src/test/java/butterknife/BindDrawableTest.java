package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindDrawableTest {
  @Test public void simple() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.graphics.drawable.Drawable;",
        "import butterknife.BindDrawable;",
        "public class Test extends Activity {",
        "  @BindDrawable(1) Drawable one;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.content.res.Resources;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.Utils;",
            "import butterknife.internal.ViewBinder;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
            "  @Override public void bind(final Finder finder, final T target, Object source) {",
            "    Context context = finder.getContext(source);",
            "    Resources res = context.getResources();",
            "    Resources.Theme theme = context.getTheme();",
            "    target.one = Utils.getDrawable(res, theme, 1);",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void withTint() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.graphics.drawable.Drawable;",
        "import butterknife.BindDrawable;",
        "public class Test extends Activity {",
        "  @BindDrawable(value = 1, tint = 2) Drawable one;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.content.res.Resources;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.Utils;",
            "import butterknife.internal.ViewBinder;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
            "  @Override public void bind(final Finder finder, final T target, Object source) {",
            "    Context context = finder.getContext(source);",
            "    Resources res = context.getResources();",
            "    Resources.Theme theme = context.getTheme();",
            "    target.one = Utils.getTintedDrawable(res, theme, 1, 2);",
            "",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void typeMustBeDrawable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.BindDrawable;",
        "public class Test extends Activity {",
        "  @BindDrawable(1) String one;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindDrawable field type must be 'Drawable'. (test.Test.one)")
        .in(source).onLine(5);
  }
}
