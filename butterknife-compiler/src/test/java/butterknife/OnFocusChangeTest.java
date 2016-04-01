package butterknife;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnFocusChangeTest {
  @Test public void focusChange() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnFocusChange;",
        "public class Test extends Activity {",
        "  @OnFocusChange(1) void doStuff() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    InnerUnbinder unbinder = createUnbinder(target);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    view.setOnFocusChangeListener(new View.OnFocusChangeListener() {\n"
        + "      @Override\n"
        + "      public void onFocusChange(View p0, boolean p1) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "    });\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  protected InnerUnbinder<T> createUnbinder(T target) {\n"
        + "    return new InnerUnbinder(target);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    private T target;\n"
        + "    View view1;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      this.target = target;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public final void unbind() {\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      unbind(target);\n"
        + "      target = null;\n"
        + "    }\n"
        + "    protected void unbind(T target) {\n"
        + "      view1.setOnFocusChangeListener(null);\n"
        + "    }\n"
        + "  }\n"
        + "}");

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
