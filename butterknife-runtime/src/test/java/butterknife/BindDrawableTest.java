package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public final class BindDrawableTest {
  @Test public void simpleSdk21() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.graphics.drawable.Drawable;\n"
        + "import butterknife.BindDrawable;\n"
        + "public class Test {\n"
        + "  @BindDrawable(1) Drawable one;\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "// Generated code from Butter Knife. Do not modify!\n"
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import java.lang.Deprecated;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
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
        + "    target.one = context.getDrawable(1);\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .withCompilerOptions("-Xlint:-processing", "-Abutterknife.minSdk=21")
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(bindingSource);
  }

  @Test public void withTint() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.graphics.drawable.Drawable;\n"
        + "import butterknife.BindDrawable;\n"
        + "public class Test {\n"
        + "  @BindDrawable(value = 1, tint = 2) Drawable one;\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "// Generated code from Butter Knife. Do not modify!\n"
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.Deprecated;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
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
        + "    target.one = Utils.getTintedDrawable(context, 1, 2);\n"
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

  @Test public void typeMustBeDrawable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.BindDrawable;\n"
        + "public class Test {\n"
        + "  @BindDrawable(1) String one;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindDrawable field type must be 'Drawable'. (test.Test.one)")
        .in(source).onLine(4);
  }
}
