package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnClickTest {
  @Test public void onClickBinding() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  @OnClick(1) void doStuff() {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.support.annotation.CallSuper;\n"
        + "import android.support.annotation.UiThread;\n"
        + "import android.view.View;\n"
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
        + "    view = Utils.findRequiredView(source, 1, \"method 'doStuff'\");\n"
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
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
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

  @Test public void onClickBindingFinalType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.OnClick;\n"
        + "public final class Test {\n"
        + "  @OnClick(1) void doStuff() {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.support.annotation.UiThread;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public final class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  private View view1;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(final Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = Utils.findRequiredView(source, 1, \"method 'doStuff'\");\n"
        + "    view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
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

  @Test public void onClickMultipleBindings() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  @OnClick(1) void doStuff1() {}\n"
        + "  @OnClick(1) void doStuff2() {}\n"
        + "  @OnClick({1, 2}) void doStuff3(View v) {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.support.annotation.CallSuper;\n"
        + "import android.support.annotation.UiThread;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  private View view1;\n"
        + "  private View view2;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(final Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = Utils.findRequiredView(source, 1, \"method 'doStuff1', method 'doStuff2', and method 'doStuff3'\");\n"
        + "    view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff1();\n"
        + "        target.doStuff2();\n"
        + "        target.doStuff3(p0);\n"
        + "      }\n"
        + "    });\n"
        + "    view = Utils.findRequiredView(source, 2, \"method 'doStuff3'\");\n"
        + "    view2 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff3(p0);\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    view1.setOnClickListener(null);\n"
        + "    view1 = null;\n"
        + "    view2.setOnClickListener(null);\n"
        + "    view2 = null;\n"
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

  @Test public void findOnlyCalledOnce() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  @BindView(1) View view;\n"
        + "  @OnClick(1) void doStuff() {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.support.annotation.CallSuper;\n"
        + "import android.support.annotation.UiThread;\n"
        + "import android.view.View;\n"
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
        + "    view = Utils.findRequiredView(source, 1, \"field 'view' and method 'doStuff'\");\n"
        + "    target.view = view;\n"
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
        + "    target.view = null;\n"
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

  @Test public void methodVisibility() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  @OnClick(1) public void thing1() {}\n"
        + "  @OnClick(2) void thing2() {}\n"
        + "  @OnClick(3) protected void thing3() {}\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .withCompilerOptions("-Xlint:-processing")
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings();
  }

  @Test public void methodCastsArgument() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.Button;\n"
        + "import android.widget.TextView;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  interface TestInterface {}\n"
        + "  @OnClick(0) void click0() {}\n"
        + "  @OnClick(1) void click1(View view) {}\n"
        + "  @OnClick(2) void click2(TextView view) {}\n"
        + "  @OnClick(3) void click3(Button button) {}\n"
        + "  @OnClick(4) void click4(TestInterface thing) {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.support.annotation.CallSuper;\n"
        + "import android.support.annotation.UiThread;\n"
        + "import android.view.View;\n"
        + "import android.widget.Button;\n"
        + "import android.widget.TextView;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  private View view0;\n"
        + "  private View view1;\n"
        + "  private View view2;\n"
        + "  private View view3;\n"
        + "  private View view4;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(final Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = Utils.findRequiredView(source, 0, \"method 'click0'\");\n"
        + "    view0 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click0();\n"
        + "      }\n"
        + "    });\n"
        + "    view = Utils.findRequiredView(source, 1, \"method 'click1'\");\n"
        + "    view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click1(p0);\n"
        + "      }\n"
        + "    });\n"
        + "    view = Utils.findRequiredView(source, 2, \"method 'click2'\");\n"
        + "    view2 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click2(Utils.castParam(p0, \"doClick\", 0, \"click2\", 0, TextView.class));\n"
        + "      }\n"
        + "    });\n"
        + "    view = Utils.findRequiredView(source, 3, \"method 'click3'\");\n"
        + "    view3 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click3(Utils.castParam(p0, \"doClick\", 0, \"click3\", 0, Button.class));\n"
        + "      }\n"
        + "    });\n"
        + "    view = Utils.findRequiredView(source, 4, \"method 'click4'\");\n"
        + "    view4 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click4(Utils.castParam(p0, \"doClick\", 0, \"click4\", 0, Test.TestInterface.class));\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    view0.setOnClickListener(null);\n"
        + "    view0 = null;\n"
        + "    view1.setOnClickListener(null);\n"
        + "    view1 = null;\n"
        + "    view2.setOnClickListener(null);\n"
        + "    view2 = null;\n"
        + "    view3.setOnClickListener(null);\n"
        + "    view3 = null;\n"
        + "    view4.setOnClickListener(null);\n"
        + "    view4 = null;\n"
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

  @Test public void methodCastsArgumentNonDebuggable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.Button;\n"
        + "import android.widget.TextView;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  interface TestInterface {}\n"
        + "  @OnClick(0) void click0() {}\n"
        + "  @OnClick(1) void click1(View view) {}\n"
        + "  @OnClick(2) void click2(TextView view) {}\n"
        + "  @OnClick(3) void click3(Button button) {}\n"
        + "  @OnClick(4) void click4(TestInterface thing) {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.support.annotation.CallSuper;\n"
        + "import android.support.annotation.UiThread;\n"
        + "import android.view.View;\n"
        + "import android.widget.Button;\n"
        + "import android.widget.TextView;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  private View view0;\n"
        + "  private View view1;\n"
        + "  private View view2;\n"
        + "  private View view3;\n"
        + "  private View view4;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(final Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = source.findViewById(0);\n"
        + "    view0 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click0();\n"
        + "      }\n"
        + "    });\n"
        + "    view = source.findViewById(1);\n"
        + "    view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click1(p0);\n"
        + "      }\n"
        + "    });\n"
        + "    view = source.findViewById(2);\n"
        + "    view2 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click2((TextView) p0);\n"
        + "      }\n"
        + "    });\n"
        + "    view = source.findViewById(3);\n"
        + "    view3 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click3((Button) p0);\n"
        + "      }\n"
        + "    });\n"
        + "    view = source.findViewById(4);\n"
        + "    view4 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click4((Test.TestInterface) p0);\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    view0.setOnClickListener(null);\n"
        + "    view0 = null;\n"
        + "    view1.setOnClickListener(null);\n"
        + "    view1 = null;\n"
        + "    view2.setOnClickListener(null);\n"
        + "    view2 = null;\n"
        + "    view3.setOnClickListener(null);\n"
        + "    view3 = null;\n"
        + "    view4.setOnClickListener(null);\n"
        + "    view4 = null;\n"
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

  @Test public void methodWithMultipleIds() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  @OnClick({1, 2, 3}) void click() {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.support.annotation.CallSuper;\n"
        + "import android.support.annotation.UiThread;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  private View view1;\n"
        + "  private View view2;\n"
        + "  private View view3;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(final Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = Utils.findRequiredView(source, 1, \"method 'click'\");\n"
        + "    view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click();\n"
        + "      }\n"
        + "    });\n"
        + "    view = Utils.findRequiredView(source, 2, \"method 'click'\");\n"
        + "    view2 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click();\n"
        + "      }\n"
        + "    });\n"
        + "    view = Utils.findRequiredView(source, 3, \"method 'click'\");\n"
        + "    view3 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.click();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    view1.setOnClickListener(null);\n"
        + "    view1 = null;\n"
        + "    view2.setOnClickListener(null);\n"
        + "    view2 = null;\n"
        + "    view3.setOnClickListener(null);\n"
        + "    view3 = null;\n"
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

  @Test public void nullable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.OnClick;\n"
        + "import butterknife.Optional;\n"
        + "public class Test {\n"
        + "  @Optional @OnClick(1) void doStuff() {}\n"
        + "}");

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.support.annotation.CallSuper;\n"
        + "import android.support.annotation.UiThread;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  private View view1;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(final Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = source.findViewById(1);\n"
        + "    if (view != null) {\n"
        + "      view1 = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.doStuff();\n"
        + "        }\n"
        + "      });\n"
        + "    }\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    if (view1 != null) {\n"
        + "      view1.setOnClickListener(null);\n"
        + "      view1 = null;\n"
        + "    }\n"
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

  @Test public void optionalAndRequiredSkipsNullCheck() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "import butterknife.OnClick;\n"
        + "import butterknife.Optional;\n"
        + "public class Test {\n"
        + "  @BindView(1) View view;\n"
        + "  @Optional @OnClick(1) void doStuff() {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.support.annotation.CallSuper;\n"
        + "import android.support.annotation.UiThread;\n"
        + "import android.view.View;\n"
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
        + "    view = Utils.findRequiredView(source, 1, \"field 'view'\");\n"
        + "    target.view = view;\n"
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
        + "    target.view = null;\n"
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

  @Test public void failsInJavaPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package java.test;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  @OnClick(1) void doStuff() {}\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick-annotated class incorrectly in Java framework package. (java.test.Test)")
        .in(source).onLine(4);
  }

  @Test public void failsInAndroidPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package android.test;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  @OnClick(1) void doStuff() {}\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick-annotated class incorrectly in Android framework package. (android.test.Test)")
        .in(source).onLine(4);
  }

  @Test public void failsIfHasReturnType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  @OnClick(1)\n"
        + "  public String doStuff() {\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@OnClick methods must have a 'void' return type. (test.Test.doStuff)")
        .in(source).onLine(5);
  }

  @Test public void failsIfPrivateMethod() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  @OnClick(1)\n"
        + "  private void doStuff() {\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@OnClick methods must not be private or static. (test.Test.doStuff)")
        .in(source).onLine(5);
  }

  @Test public void failsIfStatic() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  @OnClick(1)\n"
        + "  public static void doStuff() {\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@OnClick methods must not be private or static. (test.Test.doStuff)")
        .in(source).onLine(5);
  }

  @Test public void failsIfParameterNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  @OnClick(1)\n"
        + "  public void doStuff(String thing) {\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(""
            + "Unable to match @OnClick method arguments. (test.Test.doStuff)\n"
            + "  \n"
            + "    Parameter #1: java.lang.String\n"
            + "      did not match any listener parameters\n"
            + "  \n"
            + "  Methods may have up to 1 parameter(s):\n"
            + "  \n"
            + "    android.view.View\n"
            + "  \n"
            + "  These may be listed in any order but will be searched for from top to bottom.")
        .in(source).onLine(5);
  }

  @Test public void failsIfMoreThanOneParameter() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  @OnClick(1)\n"
        + "  public void doStuff(View thing, View otherThing) {\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick methods can have at most 1 parameter(s). (test.Test.doStuff)")
        .in(source).onLine(6);
  }

  @Test public void failsIfInInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.OnClick;\n"
        + "public interface Test {\n"
        + "  @OnClick(1)\n"
        + "  void doStuff();\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick methods may only be contained in classes. (test.Test.doStuff)")
        .in(source).onLine(3);
  }

  @Test public void failsIfHasDuplicateIds() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test {\n"
        + "  @OnClick({1, 2, 3, 1})\n"
        + "  void doStuff() {\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick annotation for method contains duplicate ID 1. (test.Test.doStuff)")
        .in(source).onLine(5);
  }
}
