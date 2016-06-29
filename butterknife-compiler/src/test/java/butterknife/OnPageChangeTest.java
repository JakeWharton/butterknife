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

    JavaFileObject binderSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public final class Test_ViewBinder implements ViewBinder<Test> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, Test target, Object source) {\n"
        + "    return new Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.support.v4.view.ViewPager;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding<T extends Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  private View view1;\n"
        + "  private ViewPager.OnPageChangeListener view1OnPageChangeListener;\n"
        + "  public Test_ViewBinding(final T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");\n"
        + "    view1 = view;\n"
        + "    view1OnPageChangeListener = new ViewPager.OnPageChangeListener() {\n"
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
        + "    };\n"
        + "    ((ViewPager) view).addOnPageChangeListener(view1OnPageChangeListener);\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    ((ViewPager) view1).removeOnPageChangeListener(view1OnPageChangeListener);\n"
        + "    view1OnPageChangeListener = null;\n"
        + "    view1 = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binderSource, bindingSource);
  }
}
