package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import javax.tools.JavaFileObject;
import org.junit.Test;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnTabChangedTest {
  @Test public void tabChangedInjection() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n')
        .join(
            "package test;",
            "import android.app.Activity;",
            "import butterknife.OnTabChanged;",
            "public class Test extends Activity {",
            "  @OnTabChanged(1) void handleTabChanged(String p0) {}",
            "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n')
            .join("package test;", 
                "import android.view.View;",
                "import butterknife.ButterKnife.Finder;", 
                "public class Test$$ViewInjector {",
                "  public static void inject(Finder finder, final test.Test target, Object source) {",
                "    View view;",
                "    view = finder.findRequiredView(source, 1, \"method 'handleTabChanged'\");",
                "    ((android.widget.TabHost) view).setOnTabChangedListener(",
                "       new android.widget.TabHost.OnTabChangeListener() {",
                "        @Override public void onTabChanged(java.lang.String p0) {",
                "          target.handleTabChanged(p0);",
                "        }",
                "     });",
                "  }",
                "  public static void reset(test.Test target) { }",
                "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError().and()
        .generatesSources(expectedSource);
  }
}
