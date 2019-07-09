package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static butterknife.TestStubs.ANDROIDX_VIEW_PAGER;
import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class OnPageChangeTest {
  @Test public void pageChange() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.OnPageChange;\n"
        + "public class Test {\n"
        + "  @OnPageChange(1) void doStuff() {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import androidx.viewpager.widget.ViewPager;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  private View view1;\n"
        + "  private ViewPager.OnPageChangeListener view1OnPageChangeListener;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(final Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = Utils.findRequiredView(source, 1, \"method 'doStuff'\");\n"
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
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    ((ViewPager) view1).removeOnPageChangeListener(view1OnPageChangeListener);\n"
        + "    view1OnPageChangeListener = null;\n"
        + "    view1 = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources())
        .that(asList(source, ANDROIDX_VIEW_PAGER))
        .withCompilerOptions("-Xlint:-processing")
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(bindingSource);
  }
}
