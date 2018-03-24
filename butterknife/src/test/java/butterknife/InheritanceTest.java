package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.common.collect.ImmutableList;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class InheritanceTest {

  @Test public void bindingViewFinalClassWithBaseClassAlreadyCompiledInDifferentModule() {
    JavaFileObject testSource = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "import butterknife.precompiled.Base;\n"
        + "public final class Test extends Base {\n"
        + "    @BindView(1) View thing;\n"
        + "}"
    );

    JavaFileObject bindingTestSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.support.annotation.UiThread;\n"
        + "import android.view.View;\n"
        + "import butterknife.internal.Utils;\n"
        + "import butterknife.precompiled.Base_ViewBinding;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public final class Test_ViewBinding extends Base_ViewBinding {\n"
        + "  private Test target;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(Test target, View source) {\n"
        + "    super(target, source);\n"
        + "    this.target = target;\n"
        + "    target.thing = Utils.findRequiredView(source, 1, \"field 'thing'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    Test target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null\n"
        + "    target.thing = null;\n"
        + "    super.unbind();\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources()).that(asList(testSource))
        .withCompilerOptions("-Xlint:-processing")
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(bindingTestSource);
  }
}
