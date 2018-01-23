package butterknife;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;
import javax.tools.JavaFileObject;
import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnValueChangeTest {
  @Test public void checkedChanged() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.OnValueChange;\n"
        + "public class Test {\n"
        + "  @OnValueChange(1) void doStuff() {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.support.annotation.CallSuper;\n"
        + "import android.support.annotation.UiThread;\n"
        + "import android.view.View;\n"
        + "import android.widget.NumberPicker;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  private View view1;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(final Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = Utils.findRequiredView(source, 1, \"method 'doStuff'\");\n"
        + "    view1 = view;\n"
        + "    ((NumberPicker) view).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {\n"
        + "      @Override\n"
        + "      public void onValueChange(NumberPicker p0, int p1, int p2) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    ((NumberPicker) view1).setOnValueChangedListener(null);\n"
        + "    view1 = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .withCompilerOptions("-Xlint:-processing")
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(bindingSource);
  }
}
