package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnPageChangeTest {
  @Test public void pageChange() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.OnPageChange;\n"
        + "public class Test extends Activity {\n"
        + "  @OnPageChange(1) void doStuff() {}\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.support.v4.view.ViewPager;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    InnerUnbinder unbinder = new InnerUnbinder(target);\n"
        + "    bindToTarget(target, finder, source, unbinder);\n"
        + "    return unbinder;\n"
        + "  }\n"
        + "  protected static void bindToTarget(final Test target, Finder finder, Object source, InnerUnbinder unbinder) {\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");\n"
        + "    unbinder.view1 = view;\n"
        + "    ((ViewPager) view).setOnPageChangeListener(new ViewPager.OnPageChangeListener() {\n"
        + "      @Override\n"
        + "      public void onPageSelected(int p0) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "      @Override\n"
        + "      public void onPageScrolled(int p0, float p1, int p2) {\n"
        + "      }\n"
        + "      @Override\n"
        + "      public void onPageScrollStateChanged(int p0) {\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    View view1;\n"
        + "    protected InnerUnbinder(T target) {\n"
        + "      this.target = target;\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      T target = this.target;\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      ((ViewPager) view1).setOnPageChangeListener(null);\n"
        + "      view1 = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
