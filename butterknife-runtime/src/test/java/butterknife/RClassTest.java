package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class RClassTest {

  private static final JavaFileObject NON_FINAL_R = JavaFileObjects.forSourceString("test.R", ""
      + "package test;\n"
      + "public final class R {\n"
      + "  public static final class array {\n"
      + "    public static int res = 0x7f040001;\n"
      + "  }\n"
      + "  public static final class bool {\n"
      + "    public static int res = 0x7f040002;\n"
      + "  }\n"
      + "  public static final class color {\n"
      + "    public static int res = 0x7f040003;\n"
      + "  }\n"
      + "  public static final class integer {\n"
      + "    public static int res = 0x7f040004;\n"
      + "  }\n"
      + "  public static final class styleable {\n"
      + "    public static int[] ActionBar = { 0x7f010001, 0x7f010003 };\n"
      + "  }\n"
      + "}"
  );

  private static final JavaFileObject FINAL_R = JavaFileObjects.forSourceString("test.R", ""
      + "package test;\n"
      + "public final class R {\n"
      + "  public static final class array {\n"
      + "    public static final int res = 0x7f040001;\n"
      + "  }\n"
      + "  public static final class bool {\n"
      + "    public static final int res = 0x7f040002;\n"
      + "  }\n"
      + "  public static final class color {\n"
      + "    public static final int res = 0x7f040003;\n"
      + "  }\n"
      + "  public static final class integer {\n"
      + "    public static final int res = 0x7f040004;\n"
      + "  }\n"
      + "  public static final class string {\n"
      + "    public static final int res = 0x7f040005;\n"
      + "  }\n"
      + "  public static final class styleable {\n"
      + "    public static final int[] ActionBar = { 0x7f010001, 0x7f010003 };\n"
      + "  }\n"
      + "}"
  );

  private static final JavaFileObject R2 = JavaFileObjects.forSourceString("test.R2", ""
      + "package test;\n"
      + "public final class R2 {\n"
      + "  public static final class array {\n"
      + "    public static final int res = 0x7f040001;\n"
      + "  }\n"
      + "  public static final class bool {\n"
      + "    public static final int res = 0x7f040002;\n"
      + "  }\n"
      + "  public static final class color {\n"
      + "    public static final int res = 0x7f040003;\n"
      + "  }\n"
      + "  public static final class integer {\n"
      + "    public static final int res = 0x7f040004;\n"
      + "  }\n"
      + "  public static final class string {\n"
      + "    public static final int res = 0x7f040005;\n"
      + "  }\n"
      + "}"
  );

  @Test public void library() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.BindInt;\n"
        + "public class Test {\n"
        + "  @BindInt(R2.integer.res) int one;\n"
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
        + "  public Test_ViewBinding(Test target, Context context) {\n"
        + "    Resources res = context.getResources();\n"
        + "    target.one = res.getInteger(R.integer.res);\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources()).that(asList(source, NON_FINAL_R, R2))
        .withCompilerOptions("-Xlint:-processing")
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(bindingSource);
  }

  @Test public void issue779() {
    JavaFileObject r2Bar = JavaFileObjects.forSourceString("test.bar.R2", ""
        + "package test.bar;\n"
        + "public final class R2 {\n"
        + "  public static final class array {\n"
        + "    public static final int res = 0x7f040001;\n"
        + "  }\n"
        + "  public static final class bool {\n"
        + "    public static final int res = 0x7f040002;\n"
        + "  }\n"
        + "  public static final class color {\n"
        + "    public static final int res = 0x7f040003;\n"
        + "  }\n"
        + "  public static final class id {\n"
        + "    public static final int res = 0x7f040004;\n"
        + "  }\n"
        + "  public static final class string {\n"
        + "    public static final int res = 0x7f040005;\n"
        + "  }\n"
        + "}");

    JavaFileObject nonFinalRBar = JavaFileObjects.forSourceString("test.bar.R", ""
        + "package test.bar;\n"
        + "public final class R {\n"
        + "  public static final class array {\n"
        + "    public static int res = 0x7f040001;\n"
        + "  }\n"
        + "  public static final class bool {\n"
        + "    public static int res = 0x7f040002;\n"
        + "  }\n"
        + "  public static final class color {\n"
        + "    public static int res = 0x7f040003;\n"
        + "  }\n"
        + "  public static final class id {\n"
        + "    public static int res = 0x7f040004;\n"
        + "  }\n"
        + "  public static final class styleable {\n"
        + "    public static int[] ActionBar = { 0x7f010001, 0x7f010003 };\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject nonFinalRFoo = JavaFileObjects.forSourceString("test.foo.R", ""
        + "package test.foo;\n"
        + "public final class R {\n"
        + "  public static final class array {\n"
        + "    public static int res = 0x7f040001;\n"
        + "  }\n"
        + "  public static final class bool {\n"
        + "    public static int res = 0x7f040002;\n"
        + "  }\n"
        + "  public static final class color {\n"
        + "    public static int res = 0x7f040003;\n"
        + "  }\n"
        + "  public static final class id {\n"
        + "    public static int bogus = 0x7f040004;\n"
        + "  }\n"
        + "  public static final class styleable {\n"
        + "    public static int[] ActionBar = { 0x7f010001, 0x7f010003 };\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject r2Foo = JavaFileObjects.forSourceString("test.foo.R2", ""
        + "package test.foo;\n"
        + "public final class R2 {\n"
        + "  public static final class array {\n"
        + "    public static final int res = 0x7f040001;\n"
        + "  }\n"
        + "  public static final class bool {\n"
        + "    public static final int res = 0x7f040002;\n"
        + "  }\n"
        + "  public static final class color {\n"
        + "    public static final int res = 0x7f040003;\n"
        + "  }\n"
        + "  public static final class id {\n"
        + "    public static final int bogus = 0x7f040004;\n"
        + "  }\n"
        + "  public static final class string {\n"
        + "    public static final int res = 0x7f040005;\n"
        + "  }\n"
        + "}");

    JavaFileObject fooSource = JavaFileObjects.forSourceString("test.foo.FooTest", ""
        + "package test.foo;\n"
        + "import android.app.Activity;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class FooTest extends Activity {\n"
        + "  @BindView(R2.id.bogus) View one;\n"
        + "}"
    );

    JavaFileObject barSource = JavaFileObjects.forSourceString("test.bar.Test", ""
        + "package test.bar;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.BindView;\n"
        + "public class Test extends Activity {\n"
        + "  @BindView(R2.id.res) CustomView one;\n"
        + "}"
    );

    JavaFileObject customView = JavaFileObjects.forSourceString("test.bar.CustomView", ""
        + "package test.bar;\n"
        + "import android.view.View;\n"
        + "import android.content.Context;\n"
        + "public class CustomView extends View {\n"
        + "  public CustomView(Context context) {\n"
        + "    super(context);"
        + "  }"
        + "}"
    );

    JavaFileObject bindingSourceBar = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "// Generated code from Butter Knife. Do not modify!\n"
        + "package test.bar;\n\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(Test target) {\n"
        + "    this(target, target.getWindow().getDecorView());\n"
        + "  }\n\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    target.one = Utils.findRequiredViewAsType(source, R.id.res, \"field 'one'\", " +
        "CustomView.class);\n"
        + "  }\n\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    Test target = this.target;\n"
        +
        "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n\n"
        + "    target.one = null;\n\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingSourceFoo = JavaFileObjects.forSourceString("test/FooTest_ViewBinding", ""
        + "// Generated code from Butter Knife. Do not modify!\n"
        + "package test.foo;\n\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class FooTest_ViewBinding implements Unbinder {\n"
        + "  private FooTest target;\n\n"
        + "  @UiThread\n"
        + "  public FooTest_ViewBinding(FooTest target) {\n"
        + "    this(target, target.getWindow().getDecorView());\n"
        + "  }\n\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(FooTest target, View source) {\n"
        + "    this.target = target;\n"
        + "    target.one = Utils.findRequiredView(source, R.id.bogus, \"field 'one'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    FooTest target = this.target;\n"
        +
        "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n\n"
        + "    this.target = null;\n\n"
        + "    target.one = null;\n\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources())
        .that(asList(customView, fooSource, barSource, nonFinalRBar, nonFinalRFoo, r2Bar, r2Foo))
        .withCompilerOptions("-Xlint:-processing")
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(bindingSourceBar);

    assertAbout(javaSources())
        .that(asList(customView, fooSource, barSource, nonFinalRBar, nonFinalRFoo, r2Bar, r2Foo))
        .withCompilerOptions("-Xlint:-processing")
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(bindingSourceFoo);
  }

  @Test public void app() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.BindBool;\n"
        + "public class Test {\n"
        + "  @BindBool(R.bool.res) boolean bool;\n"
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
        + "  public Test_ViewBinding(Test target, Context context) {\n"
        + "    Resources res = context.getResources();\n"
        + "    target.bool = res.getBoolean(R.bool.res);\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources()).that(asList(source, FINAL_R))
        .withCompilerOptions("-Xlint:-processing")
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(bindingSource);
  }

  @Test public void compiledRClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.BindColor;\n"
        + "public class Test {\n"
        + "  @BindColor(android.R.color.black) int black;\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "// Generated code from Butter Knife. Do not modify!\n"
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import androidx.core.content.ContextCompat;\n"
        + "import butterknife.Unbinder;\n"
        + "import java.lang.Deprecated;\n"
        + "import java.lang.Override;\n"
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
        + "  public Test_ViewBinding(Test target, Context context) {\n"
        + "    target.black = ContextCompat.getColor(context, android.R.color.black);\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources()).that(asList(source, NON_FINAL_R))
        .withCompilerOptions("-Xlint:-processing")
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(bindingSource);
  }
}
