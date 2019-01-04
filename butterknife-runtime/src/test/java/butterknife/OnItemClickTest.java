package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/** This augments {@link OnClickTest} with tests that exercise callbacks with parameters. */
public class OnItemClickTest {
  @Test public void onItemClickBinding() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;"
        + "import butterknife.OnItemClick;"
        + "public class Test {"
        + "  @OnItemClick(1) void doStuff() {}"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
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
        + "    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {\n"
        + "      @Override\n"
        + "      public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    ((AdapterView<?>) view1).setOnItemClickListener(null);\n"
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

  @Test public void onItemClickBindingWithParameters() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import butterknife.OnItemClick;\n"
        + "public class Test {\n"
        + "  @OnItemClick(1) void doStuff(\n"
        + "    AdapterView<?> parent,\n"
        + "    View view,\n"
        + "    int position,\n"
        + "    long id\n"
        + "  ) {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
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
        + "    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {\n"
        + "      @Override\n"
        + "      public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        target.doStuff(p0, p1, p2, p3);\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    ((AdapterView<?>) view1).setOnItemClickListener(null);\n"
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

  @Test public void onItemClickBindingWithParameterSubset() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.ListView;\n"
        + "import butterknife.OnItemClick;\n"
        + "public class Test {\n"
        + "  @OnItemClick(1) void doStuff(\n"
        + "    ListView parent,\n"
        + "    int position\n"
        + "  ) {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import android.widget.ListView;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
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
        + "    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {\n"
        + "      @Override\n"
        + "      public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        target.doStuff(Utils.castParam(p0, \"onItemClick\", 0, \"doStuff\", 0, ListView.class), p2);\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    ((AdapterView<?>) view1).setOnItemClickListener(null);\n"
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

  @Test public void onItemClickBindingWithParameterSubsetAndGenerics() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.ListView;\n"
        + "import butterknife.OnItemClick;\n"
        + "public class Test<T extends ListView> {\n"
        + "  @OnItemClick(1) void doStuff(\n"
        + "    T parent,\n"
        + "    int position\n"
        + "  ) {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import android.widget.ListView;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
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
        + "    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {\n"
        + "      @Override\n"
        + "      public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        target.doStuff(Utils.castParam(p0, \"onItemClick\", 0, \"doStuff\", 0, ListView.class)\n"
        + "        , p2);\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    ((AdapterView<?>) view1).setOnItemClickListener(null);\n"
        + "    view1 = null;\n"
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

  @Test public void onClickRootViewBinding() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.widget.ListView;\n"
        + "import butterknife.OnItemClick;\n"
        + "public class Test extends ListView {\n"
        + "  @OnItemClick void doStuff() {}\n"
        + "  public Test(Context context) {\n"
        + "    super(context);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  private View viewSource;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(Test target) {\n"
        + "    this(target, target);\n"
        + "  }\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(final Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    viewSource = source;\n"
        + "    ((AdapterView<?>) source).setOnItemClickListener(new AdapterView.OnItemClickListener() {\n"
        + "      @Override\n"
        + "      public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    ((AdapterView<?>) viewSource).setOnItemClickListener(null);\n"
        + "    viewSource = null;\n"
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

  @Test public void onClickRootViewAnyTypeBinding() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.OnItemClick;\n"
        + "public class Test {\n"
        + "  @OnItemClick void doStuff() {}\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import androidx.annotation.CallSuper;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.Unbinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  private View viewSource;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(final Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    viewSource = source;\n"
        + "    ((AdapterView<?>) source).setOnItemClickListener(new AdapterView.OnItemClickListener() {\n"
        + "      @Override\n"
        + "      public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    ((AdapterView<?>) viewSource).setOnItemClickListener(null);\n"
        + "    viewSource = null;\n"
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

  @Test public void failsWithInvalidId() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.OnItemClick;\n"
        + "public class Test {\n"
        + "  @OnItemClick({1, -1}) void doStuff() {}\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@OnItemClick annotation contains invalid ID -1. (test.Test.doStuff)")
        .in(source).onLine(4);
  }

  @Test public void failsWithInvalidParameterConfiguration() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import butterknife.OnItemClick;\n"
        + "public class Test {\n"
        + "  @OnItemClick(1) void doStuff(\n"
        + "    AdapterView<?> parent,\n"
        + "    View view,\n"
        + "    View whatIsThis\n"
        + "  ) {}\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(""
            + "Unable to match @OnItemClick method arguments. (test.Test.doStuff)\n"
            + "  \n"
            + "    Parameter #1: android.widget.AdapterView<?>\n"
            + "      matched listener parameter #1: android.widget.AdapterView<?>\n"
            + "  \n"
            + "    Parameter #2: android.view.View\n"
            + "      matched listener parameter #2: android.view.View\n"
            + "  \n"
            + "    Parameter #3: android.view.View\n"
            + "      did not match any listener parameters\n"
            + "  \n"
            + "  Methods may have up to 4 parameter(s):\n"
            + "  \n"
            + "    android.widget.AdapterView<?>\n"
            + "    android.view.View\n"
            + "    int\n"
            + "    long\n"
            + "  \n"
            + "  These may be listed in any order but will be searched for from top to bottom.")
        .in(source).onLine(6);
  }
}
