package butterknife;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnKeyTest {

    @Test
    public void onKeyTest() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
                "package test;",
                "import android.app.Activity;",
                "import butterknife.OnKey;",
                "import android.view.View;",
                "import android.view.KeyEvent;",
                "public class Test extends Activity {",
                "  @OnKey(1) boolean onKey(View v, int keyCode, KeyEvent event) {return false;}",
                "}"
        ));

        JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
                Joiner.on('\n').join(
                        "package test;",
                        "import android.view.KeyEvent;",
                        "import android.view.View;",
                        "import butterknife.internal.Finder;",
                        "import butterknife.internal.ViewBinder;",
                        "import java.lang.Object;",
                        "import java.lang.Override;",
                        "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
                        "  @Override public void bind(final Finder finder, final T target, Object source) {",
                        "    View view;",
                        "    view = finder.findRequiredView(source, 1,  \"method \'onKey\'\");",
                        "    view.setOnKeyListener(new View.OnKeyListener() { ",
                        "      @Override",
                        "      public boolean onKey(View p0, int p1, KeyEvent p2) { ",
                        "        return target.onKey(p0, p1, p2);",
                        "      }",
                        "    });",
                        "  }",
                        "}"
                ));

        assertAbout(javaSource()).that(source)
                .processedWith(new ButterKnifeProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }
}
