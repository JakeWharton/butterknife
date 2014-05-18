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
                    "import butterknife.InjectResource;",
                    "public class Test extends Activity {",
                    "    @InjectResource(1) String thing;",
                    "    @InjectResource(2) Drawable drawable;", "}"));

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
                                    "    target.thing = (java.lang.String) finder.findRequiredResource(source, 1, \"java.lang.String\", \"field 'thing'\");",
                                    "    target.drawable = (android.graphics.drawable.Drawable) finder.findRequiredResource(source, 2, \"android.graphics.drawable.Drawable\", \"field 'drawable'\");",
                                    "  }",
                                    "  public static void reset(test.Test target) {",
                                    "    target.thing = null;",
                                    "    target.drawable = null;", "  }", "}"));
    ASSERT.about(javaSource()).that(source)
            .processedWith(butterknifeProcessors()).compilesWithoutError()
            .and().generatesSources(expectedSource);
  }

}
