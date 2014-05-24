package butterknife.internal;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

import javax.tools.JavaFileObject;

import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

public class InjectResourceTest {

  @Test
  public void injectResourceTest() {
    JavaFileObject source = JavaFileObjects.forSourceString(
            "test.Test",
            Joiner.on('\n').join("package test;",
                    "import android.app.Activity;",
                    "import android.view.View;",
                    "import android.graphics.drawable.Drawable;",
                    "import butterknife.InjectString;",
                    "import butterknife.InjectDrawable;",
                    "import butterknife.InjectColor;",
                    "public class Test extends Activity {",
                    "    @InjectString(1) String thing;",
                    "    @InjectColor(2) int color;",
                    "    @InjectDrawable(3) Drawable drawable;", "}"));

    JavaFileObject expectedSource = JavaFileObjects
            .forSourceString(
                    "test/Test$$ViewInjector",
                    Joiner.on('\n')
                            .join("package test;",
                                    "import android.view.View;",
                                    "import butterknife.ButterKnife.Finder;",
                                    "public class Test$$ViewInjector {",
                                    "  public static void inject(Finder finder, final test.Test target, Object source) {",
                                    "    View view;",
                                    "    target.thing = finder.getResources(source).getString(1);",
                                    "    target.color = finder.getResources(source).getColor(2);",
                                    "    target.drawable = finder.getResources(source).getDrawable(3);",
                                    "  }",
                                    "  public static void reset(test.Test target) {",
                                    "  }",
                                    "}"
                                    ));
    ASSERT.about(javaSource()).that(source)
            .processedWith(butterknifeProcessors()).compilesWithoutError()
            .and().generatesSources(expectedSource);
  }

}
