package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class ExtendViewTest {
  @Test public void onlyResources() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;"
        + "import android.content.Context;"
        + "import android.view.View;"
        + "import butterknife.BindBool;"
        + "public class Test extends View {"
        + "  Test(Context context) {"
        + "    super(context);"
        + "  }"
        + "  @BindBool(1) boolean one;"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "// Generated code from Butter Knife. Do not modify!\n"
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import java.lang.Deprecated;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(Test target) {\n"
        + "    this(target, target.getContext());\n"
        + "  }\n"
        + "  /**\n"
        + "   * @deprecated Use {@link #Test_ViewBinding(Test, Context)} for direct creation.\n"
        + "   *     Only present for runtime invocation through {@code ButterKnife.bind()}.\n"
        + "   */\n"
        + "  @Deprecated\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(Test target, View source) {\n"
        + "    this(target, source.getContext());\n"
        + "  }\n"
        + "  @UiThread\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public Test_ViewBinding(Test target, Context context) {\n"
        + "    Resources res = context.getResources();\n"
        + "    target.one = res.getBoolean(1);\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
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

  @Test public void views() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;"
        + "import android.content.Context;"
        + "import android.view.View;"
        + "import butterknife.BindView;"
        + "public class Test extends View {"
        + "  Test(Context context) {"
        + "    super(context);"
        + "  }"
        + "  @BindView(1) View one;"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(Test target) {\n"
        + "    this(target, target);\n"
        + "  }\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    target.one = Utils.findRequiredView(source, 1, \"field 'one'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    Test target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n"
        + "    target.one = null;\n"
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
