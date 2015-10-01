package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnTextChangedTest {
  @Test public void textChanged() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnTextChanged;",
        "public class Test extends Activity {",
        "  @OnTextChanged(1) void doStuff() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.text.Editable;",
            "import android.text.TextWatcher;",
            "import android.view.View;",
            "import android.widget.TextView;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.ViewBinder;",
            "import java.lang.CharSequence;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
            "  @Override public void bind(final Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((TextView) view).addTextChangedListener(new TextWatcher() {",
            "      @Override public void onTextChanged(CharSequence p0, int p1, int p2, int p3) {",
            "        target.doStuff();",
            "      }",
            "      @Override public void beforeTextChanged(CharSequence p0, int p1, int p2, int p3) {",
            "      }",
            "      @Override public void afterTextChanged(Editable p0) {",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
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
