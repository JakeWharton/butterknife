package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnEditorActionTest {
  @Test public void editorAction() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnEditorAction;",
        "public class Test extends Activity {",
        "  @OnEditorAction(1) boolean doStuff() { return false; }",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.ViewBinder;",
            "public class Test$$ViewBinder<T extends test.Test> implements ViewBinder<T> {",
            "  @Override public void bind(final Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((android.widget.TextView) view).setOnEditorActionListener(new android.widget.TextView.OnEditorActionListener() {",
            "      @Override public boolean onEditorAction(android.widget.TextView p0, int p1, android.view.KeyEvent p2) {",
            "        return target.doStuff();",
            "      }",
            "    });",
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
}
