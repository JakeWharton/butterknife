package butterknife;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
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
            "import android.view.KeyEvent;",
            "import android.view.View;",
            "import android.widget.TextView;",
            "import butterknife.Unbinder;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.ViewBinder;",
            "import java.lang.IllegalStateException;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
            "  @Override",
            "  public Unbinder bind(final Finder finder, final T target, Object source) {",
            "    InnerUnbinder unbinder = createUnbinder(target);",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    unbinder.view1 = view;",
            "    ((TextView) view).setOnEditorActionListener(new TextView.OnEditorActionListener() {",
            "      @Override",
            "      public boolean onEditorAction(TextView p0, int p1, KeyEvent p2) {",
            "        return target.doStuff();",
            "      }",
            "    });",
            "    return unbinder;",
            "  }",
            "  protected InnerUnbinder<T> createUnbinder(T target) {",
            "    return new InnerUnbinder(target);",
            "  }",
            "  protected static class InnerUnbinder<T extends Test> implements Unbinder {",
            "    private T target;",
            "    View view1;",
            "    protected InnerUnbinder(T target) {",
            "      this.target = target;",
            "    }",
            "    @Override",
            "    public final void unbind() {",
            "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");",
            "      unbind(target);",
            "      target = null;",
            "    }",
            "    protected void unbind(T target) {",
            "      ((TextView) view1).setOnEditorActionListener(null);",
            "    }",
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
