package butterknife;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnItemLongClickTest {
  @Test public void itemLongClick() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnItemLongClick;",
        "public class Test extends Activity {",
        "  @OnItemLongClick(1) boolean doStuff() { return false; }",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    InnerUnbinder unbinder = createUnbinder(target);\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    ((AdapterView<?>) view).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {\n"
        + "      @Override\n"
        + "      public boolean onItemLongClick(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        return target.doStuff();\n"
        + "      }\n"
        + "    });\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  @SuppressWarnings(\"unchecked\")\n"
        + "  protected <U extends InnerUnbinder<T>> U createUnbinder(T target) {\n"
        + "    return (U) new InnerUnbinder(target);\n"
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
        + "      ((AdapterView<?>) view1).setOnItemLongClickListener(null);\n"
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
