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

public class BindViewTest {
  @Test public void bindingViewNonDebuggable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class Test {\n"
        + "    @BindView(1) View thing;\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    target.thing = source.findViewById(1);\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    Test target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n"
        + "    target.thing = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .withCompilerOptions("-Xlint:-processing", "-Abutterknife.debuggable=false")
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(bindingSource);
  }

  @Test public void bindingViewSubclassNonDebuggable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.widget.TextView;\n"
        + "import butterknife.BindView;\n"
        + "public class Test {\n"
        + "    @BindView(1) TextView thing;\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.TextView;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    target.thing = (TextView) source.findViewById(1);\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    Test target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n"
        + "    target.thing = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .withCompilerOptions("-Xlint:-processing", "-Abutterknife.debuggable=false")
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(bindingSource);
  }

  @Test public void bindingGeneratedView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.BindView;\n"
        + "@PerformGeneration\n"
        + "public class Test {\n"
        + "    @BindView(1) GeneratedView thing;\n"
        + "}"
    );

    // w/o the GeneratingProcessor it can't find `class GeneratedView`
    assertAbout(javaSources()).that(ImmutableList.of(source, TestGeneratingProcessor.ANNOTATION))
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("cannot find symbol");

    // now the GeneratingProcessor should let it compile
    assertAbout(javaSources()).that(ImmutableList.of(source, TestGeneratingProcessor.ANNOTATION))
        .processedWith(new ButterKnifeProcessor(), new TestGeneratingProcessor("GeneratedView",
            "package test;",
            "import android.content.Context;",
            "import android.view.View;",
            "public class GeneratedView extends View {",
            "  public GeneratedView(Context context) {",
            "    super(context);",
            "  }",
            "}"
        ))
        .compilesWithoutError()
        .withNoteContaining("@BindView field with unresolved type (GeneratedView)").and()
        .withNoteContaining("must elsewhere be generated as a View or interface").and()
        .and()
        .generatesFileNamed(StandardLocation.CLASS_OUTPUT, "test", "Test_ViewBinding.class");
  }

  @Test public void bindingViewFinalClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public final class Test {\n"
        + "    @BindView(1) View thing;\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public final class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    target.thing = Utils.findRequiredView(source, 1, \"field 'thing'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    Test target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n"
        + "    target.thing = null;\n"
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

  @Test public void bindingViewFinalClassWithBaseClass() {
    JavaFileObject baseSource = JavaFileObjects.forSourceString("test.Base", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class Base {\n"
        + "    @BindView(1) View thing;\n"
        + "}"
    );
    JavaFileObject testSource = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public final class Test extends Base {\n"
        + "    @BindView(1) View thing;\n"
        + "}"
    );

    JavaFileObject bindingBaseSource = JavaFileObjects.forSourceString("test/Base_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class Base_ViewBinding implements Unbinder {\n"
        + "  private Base target;\n"
        + "  @UiThread\n"
        + "  public Base_ViewBinding(Base target, View source) {\n"
        + "    this.target = target;\n"
        + "    target.thing = Utils.findRequiredView(source, 1, \"field 'thing'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    Base target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n"
        + "    target.thing = null;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingTestSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.internal.Utils;\n"
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

    assertAbout(javaSources()).that(asList(baseSource, testSource))
        .withCompilerOptions("-Xlint:-processing")
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(bindingBaseSource, bindingTestSource);
  }

  @Test public void bindingViewUppercasePackageName() {
    JavaFileObject source = JavaFileObjects.forSourceString("com.Example.Test", ""
        + "package com.Example;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class Test {\n"
        + "    @BindView(1) View thing;\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package com.Example;\n"
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
        + "  public Test_ViewBinding(Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    target.thing = Utils.findRequiredView(source, 1, \"field 'thing'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    Test target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n"
        + "    target.thing = null;\n"
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

  @Test public void bindingInterface() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class Test {\n"
        + "    interface TestInterface {}\n"
        + "    @BindView(1) TestInterface thing;\n"
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
        + "  public Test_ViewBinding(Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    target.thing = Utils.findRequiredViewAsType(source, 1, \"field 'thing'\", Test.TestInterface.class);\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    Test target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n"
        + "    target.thing = null;\n"
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

  @Test public void genericType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.widget.EditText;\n"
        + "import android.widget.TextView;\n"
        + "import butterknife.BindView;\n"
        + "class Test<T extends TextView> {\n"
        + "    @BindView(1) T thing;\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.TextView;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    target.thing = Utils.findRequiredViewAsType(source, 1, \"field 'thing'\", TextView.class);\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    Test target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n"
        + "    target.thing = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .withCompilerOptions("-Xlint:-processing")
        .processedWith(new ButterKnifeProcessor())
        // found raw type: test.Test
        //   missing type arguments for generic class test.Test<T>
        .compilesWithoutError()
        .and()
        .generatesSources(bindingSource);
  }

  @Test public void oneFindPerId() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  @BindView(1) View thing1;\n"
        + "  @OnClick(1) void doStuff() {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
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
        + "    view = Utils.findRequiredView(source, 1, \"field 'thing1' and method 'doStuff'\");\n"
        + "    target.thing1 = view;\n"
        + "    view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    Test target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n"
        + "    target.thing1 = null;\n"
        + "    view1.setOnClickListener(null);\n"
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

  @Test public void oneFindPerIdWithCast() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.widget.Button;\n"
        + "import butterknife.BindView;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  @BindView(1) Button thing1;\n"
        + "  @OnClick(1) void doStuff() {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.Button;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
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
        + "    view = Utils.findRequiredView(source, 1, \"field 'thing1' and method 'doStuff'\");\n"
        + "    target.thing1 = Utils.castView(view, 1, \"field 'thing1'\", Button.class);\n"
        + "    view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    Test target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n"
        + "    target.thing1 = null;\n"
        + "    view1.setOnClickListener(null);\n"
        + "    view1 = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .withCompilerOptions("-Xlint:-processing")
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(bindingSource);
  }

  @Test public void fieldVisibility() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class Test {\n"
        + "  @BindView(1) public View thing1;\n"
        + "  @BindView(2) View thing2;\n"
        + "  @BindView(3) protected View thing3;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .withCompilerOptions("-Xlint:-processing")
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings();
  }

  @Test public void nullable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class Test {\n"
        + "  @interface Nullable {}\n"
        + "  @Nullable @BindView(1) View view;\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    target.view = source.findViewById(1);\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    Test target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n"
        + "    target.view = null;\n"
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

  @Test public void superclass() {
    JavaFileObject source1 = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class Test {\n"
        + "  @BindView(1) View view;\n"
        + "}"
    );

    JavaFileObject source2 = JavaFileObjects.forSourceString("test.TestOne", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class TestOne extends Test {\n"
        + "  @BindView(1) View thing;\n"
        + "}"
    );

    JavaFileObject source3 = JavaFileObjects.forSourceString("test.TestTwo", ""
        + "package test;\n"
        + "public class TestTwo extends Test {\n"
        + "}"
    );

    JavaFileObject binding1Source = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
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
        + "  public Test_ViewBinding(Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    target.view = Utils.findRequiredView(source, 1, \"field 'view'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    Test target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n"
        + "    target.view = null;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binding2Source = JavaFileObjects.forSourceString("test/TestOne_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class TestOne_ViewBinding extends Test_ViewBinding {\n"
        + "  private TestOne target;\n"
        + "  @UiThread\n"
        + "  public TestOne_ViewBinding(TestOne target, View source) {\n"
        + "    super(target, source);\n"
        + "    this.target = target;\n"
        + "    target.thing = Utils.findRequiredView(source, 1, \"field 'thing'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    TestOne target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n"
        + "    target.thing = null;\n"
        + "    super.unbind();\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources()).that(asList(source1, source2, source3))
        .withCompilerOptions("-Xlint:-processing")
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(binding1Source, binding2Source);
  }

  @Test public void genericSuperclass() {
    JavaFileObject source1 = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class Test<T> {\n"
        + "  @BindView(1) View view;\n"
        + "}"
    );

    JavaFileObject source2 = JavaFileObjects.forSourceString("test.TestOne", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class TestOne extends Test<String> {\n"
        + "  @BindView(1) View thing;\n"
        + "}"
    );

    JavaFileObject source3 = JavaFileObjects.forSourceString("test.TestTwo", ""
        + "package test;\n"
        + "public class TestTwo extends Test<Object> {\n"
        + "}"
    );

    JavaFileObject binding1Source = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
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
        + "  public Test_ViewBinding(Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    target.view = Utils.findRequiredView(source, 1, \"field 'view'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    Test target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n"
        + "    target.view = null;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binding2Source = JavaFileObjects.forSourceString("test/TestOne_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class TestOne_ViewBinding extends Test_ViewBinding {\n"
        + "  private TestOne target;\n"
        + "  @UiThread\n"
        + "  public TestOne_ViewBinding(TestOne target, View source) {\n"
        + "    super(target, source);\n"
        + "    this.target = target;\n"
        + "    target.thing = Utils.findRequiredView(source, 1, \"field 'thing'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    TestOne target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null\n"
        + "    target.thing = null;\n"
        + "    super.unbind();\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources()).that(asList(source1, source2, source3))
        .withCompilerOptions("-Xlint:-processing")
        .processedWith(new ButterKnifeProcessor())
        // found raw type: test.Test
        //   missing type arguments for generic class test.Test<T>
        .compilesWithoutError()
        .and()
        .generatesSources(binding1Source, binding2Source);
  }

  @Test public void failsInJavaPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package java.test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class Test {\n"
        + "  @BindView(1) View thing;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindView-annotated class incorrectly in Java framework package. (java.test.Test)")
        .in(source).onLine(5);
  }

  @Test public void failsInAndroidPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package android.test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class Test {\n"
        + "  @BindView(1) View thing;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindView-annotated class incorrectly in Android framework package. (android.test.Test)")
        .in(source).onLine(5);
  }

  @Test public void failsIfInPrivateClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class Test {\n"
        + "  private static class Inner {\n"
        + "    @BindView(1) View thing;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindView fields may not be contained in private classes. (test.Test.Inner.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.BindView;\n"
        + "public class Test {\n"
        + "  @BindView(1) String thing;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindView fields must extend from View or be an interface. (test.Test.thing)")
        .in(source).onLine(4);
  }

  @Test public void failsIfInInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public interface Test {\n"
        + "    @BindView(1) View thing = null;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindView fields may only be contained in classes. (test.Test.thing)")
        .in(source).onLine(4);
  }

  @Test public void failsIfPrivate() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class Test {\n"
        + "    @BindView(1) private View thing;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindView fields must not be private or static. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfStatic() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class Test {\n"
        + "    @BindView(1) static View thing;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindView fields must not be private or static. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void duplicateBindingFails() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class Test {\n"
        + "    @BindView(1) View thing1;\n"
        + "    @BindView(1) View thing2;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "Attempt to use @BindView for an already bound ID 1 on 'thing1'. (test.Test.thing2)")
        .in(source).onLine(6);
  }

  @Test public void failsOptionalRootViewBinding() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.view.View;\n"
        + "import butterknife.OnClick;\n"
        + "import butterknife.Optional;\n"
        + "public class Test extends View {\n"
        + "  @Optional @OnClick void doStuff() {}\n"
        + "  public Test(Context context) {\n"
        + "    super(context);\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource())
        .that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "ID-free binding must not be annotated with @Optional. (test.Test.doStuff)")
        .in(source)
        .onLine(7);
  }

}
