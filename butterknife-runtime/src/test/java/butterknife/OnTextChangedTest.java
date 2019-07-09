package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnTextChangedTest {
  @Test public void textChanged() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.OnTextChanged;\n"
        + "public class Test {\n"
        + "  @OnTextChanged(1) void doStuff() {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.text.Editable;\n"
        + "import android.text.TextWatcher;\n"
        + "import android.view.View;\n"
        + "import android.widget.TextView;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.CharSequence;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  private View view1;\n"
        + "  private TextWatcher view1TextWatcher;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(final Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = Utils.findRequiredView(source, 1, \"method 'doStuff'\");\n"
        + "    view1 = view;\n"
        + "    view1TextWatcher = new TextWatcher() {\n"
        + "      @Override\n"
        + "      public void onTextChanged(CharSequence p0, int p1, int p2, int p3) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "      @Override\n"
        + "      public void beforeTextChanged(CharSequence p0, int p1, int p2, int p3) {\n"
        + "      }\n"
        + "      @Override\n"
        + "      public void afterTextChanged(Editable p0) {\n"
        + "      }\n"
        + "    };\n"
        + "    ((TextView) view).addTextChangedListener(view1TextWatcher);\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    ((TextView) view1).removeTextChangedListener(view1TextWatcher);\n"
        + "    view1TextWatcher = null;\n"
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

  @Test public void textChangedWithParameter() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import butterknife.OnTextChanged;\n"
            + "public class Test {\n"
            + "  @OnTextChanged(1) void doStuff(CharSequence p0) {}\n"
            + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
            + "package test;\n"
            + "import android.text.Editable;\n"
            + "import android.text.TextWatcher;\n"
            + "import android.view.View;\n"
            + "import android.widget.TextView;\n"
            + "import androidx.annotation.CallSuper;\n"
            + "import androidx.annotation.UiThread;\n"
            + "import butterknife.Unbinder;\n"
            + "import butterknife.internal.Utils;\n"
            + "import java.lang.CharSequence;\n"
            + "import java.lang.IllegalStateException;\n"
            + "import java.lang.Override;\n"
            + "public class Test_ViewBinding implements Unbinder {\n"
            + "  private Test target;\n"
            + "  private View view1;\n"
            + "  private TextWatcher view1TextWatcher;\n"
            + "  @UiThread\n"
            + "  public Test_ViewBinding(final Test target, View source) {\n"
            + "    this.target = target;\n"
            + "    View view;\n"
            + "    view = Utils.findRequiredView(source, 1, \"method 'doStuff'\");\n"
            + "    view1 = view;\n"
            + "    view1TextWatcher = new TextWatcher() {\n"
            + "      @Override\n"
            + "      public void onTextChanged(CharSequence p0, int p1, int p2, int p3) {\n"
            + "        target.doStuff(p0);\n"
            + "      }\n"
            + "      @Override\n"
            + "      public void beforeTextChanged(CharSequence p0, int p1, int p2, int p3) {\n"
            + "      }\n"
            + "      @Override\n"
            + "      public void afterTextChanged(Editable p0) {\n"
            + "      }\n"
            + "    };\n"
            + "    ((TextView) view).addTextChangedListener(view1TextWatcher);\n"
            + "  }\n"
            + "  @Override\n"
            + "  @CallSuper\n"
            + "  public void unbind() {\n"
            + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
            + "    target = null;\n"
            + "    ((TextView) view1).removeTextChangedListener(view1TextWatcher);\n"
            + "    view1TextWatcher = null;\n"
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

  @Test public void textChangedWithParameters() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import butterknife.OnTextChanged;\n"
            + "public class Test {\n"
            + "  @OnTextChanged(1) void doStuff(CharSequence p0, int p1, int p2, int p3) {}\n"
            + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
            + "package test;\n"
            + "import android.text.Editable;\n"
            + "import android.text.TextWatcher;\n"
            + "import android.view.View;\n"
            + "import android.widget.TextView;\n"
            + "import androidx.annotation.CallSuper;\n"
            + "import androidx.annotation.UiThread;\n"
            + "import butterknife.Unbinder;\n"
            + "import butterknife.internal.Utils;\n"
            + "import java.lang.CharSequence;\n"
            + "import java.lang.IllegalStateException;\n"
            + "import java.lang.Override;\n"
            + "public class Test_ViewBinding implements Unbinder {\n"
            + "  private Test target;\n"
            + "  private View view1;\n"
            + "  private TextWatcher view1TextWatcher;\n"
            + "  @UiThread\n"
            + "  public Test_ViewBinding(final Test target, View source) {\n"
            + "    this.target = target;\n"
            + "    View view;\n"
            + "    view = Utils.findRequiredView(source, 1, \"method 'doStuff'\");\n"
            + "    view1 = view;\n"
            + "    view1TextWatcher = new TextWatcher() {\n"
            + "      @Override\n"
            + "      public void onTextChanged(CharSequence p0, int p1, int p2, int p3) {\n"
            + "        target.doStuff(p0, p1, p2, p3);\n"
            + "      }\n"
            + "      @Override\n"
            + "      public void beforeTextChanged(CharSequence p0, int p1, int p2, int p3) {\n"
            + "      }\n"
            + "      @Override\n"
            + "      public void afterTextChanged(Editable p0) {\n"
            + "      }\n"
            + "    };\n"
            + "    ((TextView) view).addTextChangedListener(view1TextWatcher);\n"
            + "  }\n"
            + "  @Override\n"
            + "  @CallSuper\n"
            + "  public void unbind() {\n"
            + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
            + "    target = null;\n"
            + "    ((TextView) view1).removeTextChangedListener(view1TextWatcher);\n"
            + "    view1TextWatcher = null;\n"
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

  @Test public void textChangedWithWrongParameter() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
            + "package test;\n"
            + "import butterknife.OnTextChanged;\n"
            + "public class Test {\n"
            + "  @OnTextChanged(1) void doStuff(String p0, int p1, int p2, int p3) {}\n"
            + "}"
    );

    assertAbout(javaSource()).that(source)
            .withCompilerOptions("-Xlint:-processing")
            .processedWith(new ButterKnifeProcessor())
            .failsToCompile();
  }

}
