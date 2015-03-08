package butterknife.compiler;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static butterknife.compiler.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.common.truth.Truth.ASSERT;
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

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.Injector;",
            "public class Test$$ViewInjector<T extends test.Test> implements Injector<T> {",
            "  @Override public void inject(final Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((android.widget.TextView) view).addTextChangedListener(new android.text.TextWatcher() {",
            "      @Override public void onTextChanged(java.lang.CharSequence p0, int p1, int p2, int p3) {",
            "        target.doStuff();",
            "      }",
            "      @Override public void beforeTextChanged(java.lang.CharSequence p0, int p1, int p2, int p3) {",
            "      }",
            "      @Override public void afterTextChanged(android.text.Editable p0) {",
            "      }",
            "    });",
            "  }",
            "  @Override public void reset(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
