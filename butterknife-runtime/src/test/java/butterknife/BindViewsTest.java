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

public class BindViewsTest {
  @Test public void bindingArrayWithGenerics() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindViews;\n"
        + "public class Test<T extends View> {\n"
        + "    @BindViews({1, 2, 3}) T[] thing;\n"
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
        + "    target.thing = Utils.arrayFilteringNull(\n"
        + "        Utils.findRequiredView(source, 1, \"field 'thing'\"), \n"
        + "        Utils.findRequiredView(source, 2, \"field 'thing'\"), \n"
        + "        Utils.findRequiredView(source, 3, \"field 'thing'\"));\n"
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

  @Test public void bindingArrayWithCast() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.widget.TextView;\n"
        + "import butterknife.BindViews;\n"
        + "public class Test {\n"
        + "    @BindViews({1, 2, 3}) TextView[] thing;\n"
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
        + "    target.thing = Utils.arrayFilteringNull(\n"
        + "        Utils.findRequiredViewAsType(source, 1, \"field 'thing'\", TextView.class), \n"
        + "        Utils.findRequiredViewAsType(source, 2, \"field 'thing'\", TextView.class), \n"
        + "        Utils.findRequiredViewAsType(source, 3, \"field 'thing'\", TextView.class));\n"
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

  @Test public void bindingArrayNonDebuggable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindViews;\n"
        + "public class Test {\n"
        + "    @BindViews({1, 2, 3}) View[] thing;\n"
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
        + "    target.thing = Utils.arrayFilteringNull(\n"
        + "        source.findViewById(1), \n"
        + "        source.findViewById(2), \n"
        + "        source.findViewById(3));\n"
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

  @Test public void bindingArrayWithCastNonDebuggable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.widget.TextView;\n"
        + "import butterknife.BindViews;\n"
        + "public class Test {\n"
        + "    @BindViews({1, 2, 3}) TextView[] thing;\n"
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
        + "    target.thing = Utils.arrayFilteringNull(\n"
        + "        (TextView) source.findViewById(1), \n"
        + "        (TextView) source.findViewById(2), \n"
        + "        (TextView) source.findViewById(3));\n"
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
        + "import butterknife.BindViews;\n"
        + "import java.util.List;\n"
        + "@PerformGeneration\n"
        + "public class Test {\n"
        + "    @BindViews({1, 2}) List<GeneratedView> things;\n"
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
        .withNoteContaining("@BindViews List or array with unresolved type (GeneratedView)").and()
        .withNoteContaining("must elsewhere be generated as a View or interface").and()
        .and()
        .generatesFileNamed(StandardLocation.CLASS_OUTPUT, "test", "Test_ViewBinding.class");
  }

  @Test public void bindingListOfInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.BindViews;\n"
        + "import java.util.List;\n"
        + "public class Test {\n"
        + "    interface TestInterface {}\n"
        + "    @BindViews({1, 2, 3}) List<TestInterface> thing;\n"
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
        + "    target.thing = Utils.listFilteringNull(\n"
        + "        Utils.findRequiredViewAsType(source, 1, \"field 'thing'\", Test.TestInterface.class), \n"
        + "        Utils.findRequiredViewAsType(source, 2, \"field 'thing'\", Test.TestInterface.class), \n"
        + "        Utils.findRequiredViewAsType(source, 3, \"field 'thing'\", Test.TestInterface.class));\n"
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

  @Test public void bindingListWithGenerics() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindViews;\n"
        + "import java.util.List;\n"
        + "public class Test<T extends View> {\n"
        + "    @BindViews({1, 2, 3}) List<T> thing;\n"
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
        + "    target.thing = Utils.listFilteringNull(\n"
        + "        Utils.findRequiredView(source, 1, \"field 'thing'\"), \n"
        + "        Utils.findRequiredView(source, 2, \"field 'thing'\"), \n"
        + "        Utils.findRequiredView(source, 3, \"field 'thing'\"));\n"
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

  @Test public void nullableList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindViews;\n"
        + "import java.util.List;\n"
        + "public class Test {\n"
        + "    @interface Nullable {}\n"
        + "    @Nullable @BindViews({1, 2, 3}) List<View> thing;\n"
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
        + "    target.thing = Utils.listFilteringNull(\n"
        + "        source.findViewById(1), \n"
        + "        source.findViewById(2), \n"
        + "        source.findViewById(3));\n"
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
        .generatesSources( bindingSource);
  }

  @Test public void failsIfNoIds() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindViews;\n"
        + "import java.util.List;\n"
        + "public class Test {\n"
        + "  @BindViews({}) List<View> thing;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindViews must specify at least one ID. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfNoGenericType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.BindViews;\n"
        + "import java.util.List;\n"
        + "public class Test {\n"
        + "  @BindViews(1) List thing;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindViews List must have a generic component. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfUnsupportedCollection() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindViews;\n"
        + "import java.util.Deque;\n"
        + "public class Test {\n"
        + "  @BindViews(1) Deque<View> thing;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindViews must be a List or array. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfGenericNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.BindViews;\n"
        + "import java.util.List;\n"
        + "public class Test {\n"
        + "  @BindViews(1) List<String> thing;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindViews List or array type must extend from View or be an interface. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfArrayNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.BindViews;\n"
        + "public class Test {\n"
        + "  @BindViews(1) String[] thing;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindViews List or array type must extend from View or be an interface. (test.Test.thing)")
        .in(source).onLine(4);
  }

  @Test public void failsIfContainsDuplicateIds() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindViews;\n"
        + "import java.util.List;\n"
        + "public class Test {\n"
        + "    @BindViews({1, 1}) List<View> thing;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindViews annotation contains duplicate ID 1. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void bindingArrayWithRScanner() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.R;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindViews;\n"
        + "public class Test {\n"
        + "    @BindViews({R.color.black, R.color.white}) View[] thing;\n"
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
        + "    target.thing = Utils.arrayFilteringNull(\n"
        + "        Utils.findRequiredView(source, android.R.color.black, \"field 'thing'\"), \n"
        + "        Utils.findRequiredView(source, android.R.color.white, \"field 'thing'\"));\n"
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

  @Test public void bindingArrayWithMixedRAndLiteral() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.R;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindViews;\n"
        + "public class Test {\n"
        + "    @BindViews({R.color.black, 2, R.color.white}) View[] thing;\n"
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
        + "    target.thing = Utils.arrayFilteringNull(\n"
        + "        Utils.findRequiredView(source, android.R.color.black, \"field 'thing'\"), \n"
        + "        Utils.findRequiredView(source, 2, \"field 'thing'\"), \n"
        + "        Utils.findRequiredView(source, android.R.color.white, \"field 'thing'\"));\n"
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
}
