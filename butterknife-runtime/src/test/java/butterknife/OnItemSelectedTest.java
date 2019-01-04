package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/** This augments {@link OnClickTest} with tests that exercise callbacks with multiple methods. */
public class OnItemSelectedTest {
  @Test public void defaultMethod() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.OnItemSelected;\n"
        + "public class Test {\n"
        + "  @OnItemSelected(1) void doStuff() {}\n"
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
        + "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {\n"
        + "      @Override\n"
        + "      public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "      @Override\n"
        + "      public void onNothingSelected(AdapterView<?> p0) {\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    ((AdapterView<?>) view1).setOnItemSelectedListener(null);\n"
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

  @Test public void nonDefaultMethod() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;"
        + "import butterknife.OnItemSelected;"
        + "import static butterknife.OnItemSelected.Callback.NOTHING_SELECTED;"
        + "public class Test {"
        + "  @OnItemSelected(value = 1, callback = NOTHING_SELECTED)"
        + "  void doStuff() {}"
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
        + "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {\n"
        + "      @Override\n"
        + "      public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "      }\n"
        + "      @Override\n"
        + "      public void onNothingSelected(AdapterView<?> p0) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    ((AdapterView<?>) view1).setOnItemSelectedListener(null);\n"
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

  @Test public void allMethods() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;"
        + "import butterknife.OnItemSelected;"
        + "import static butterknife.OnItemSelected.Callback.NOTHING_SELECTED;"
        + "public class Test {"
        + "  @OnItemSelected(1)"
        + "  void onItemSelected() {}"
        + "  @OnItemSelected(value = 1, callback = NOTHING_SELECTED)"
        + "  void onNothingSelected() {}"
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
        + "    view = Utils.findRequiredView(source, 1, \"method 'onItemSelected' and method 'onNothingSelected'\");\n"
        + "    view1 = view;\n"
        + "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {\n"
        + "      @Override\n"
        + "      public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        target.onItemSelected();\n"
        + "      }\n"
        + "      @Override\n"
        + "      public void onNothingSelected(AdapterView<?> p0) {\n"
        + "        target.onNothingSelected();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    ((AdapterView<?>) view1).setOnItemSelectedListener(null);\n"
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

  @Test public void multipleBindingPermutation() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;"
        + "import butterknife.OnItemSelected;"
        + "import static butterknife.OnItemSelected.Callback.NOTHING_SELECTED;"
        + "public class Test {"
        + "  @OnItemSelected({ 1, 2 })"
        + "  void onItemSelected() {}"
        + "  @OnItemSelected(value = { 1, 3 }, callback = NOTHING_SELECTED)"
        + "  void onNothingSelected() {}"
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
        + "  private View view2;\n"
        + "  private View view3;\n"
        + "  @UiThread\n"
        + "  public Test_ViewBinding(final Test target, View source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = Utils.findRequiredView(source, 1, \"method 'onItemSelected' and method 'onNothingSelected'\");\n"
        + "    view1 = view;\n"
        + "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {\n"
        + "      @Override\n"
        + "      public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        target.onItemSelected();\n"
        + "      }\n"
        + "      @Override\n"
        + "      public void onNothingSelected(AdapterView<?> p0) {\n"
        + "        target.onNothingSelected();\n"
        + "      }\n"
        + "    });\n"
        + "    view = Utils.findRequiredView(source, 2, \"method 'onItemSelected'\");\n"
        + "    view2 = view;\n"
        + "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {\n"
        + "      @Override\n"
        + "      public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        target.onItemSelected();\n"
        + "      }\n"
        + "      @Override\n"
        + "      public void onNothingSelected(AdapterView<?> p0) {\n"
        + "      }\n"
        + "    });\n"
        + "    view = Utils.findRequiredView(source, 3, \"method 'onNothingSelected'\");\n"
        + "    view3 = view;\n"
        + "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {\n"
        + "      @Override\n"
        + "      public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "      }\n"
        + "      @Override\n"
        + "      public void onNothingSelected(AdapterView<?> p0) {\n"
        + "        target.onNothingSelected();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  @CallSuper\n"
        + "  public void unbind() {\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target = null;\n"
        + "    ((AdapterView<?>) view1).setOnItemSelectedListener(null);\n"
        + "    view1 = null;\n"
        + "    ((AdapterView<?>) view2).setOnItemSelectedListener(null);\n"
        + "    view2 = null;\n"
        + "    ((AdapterView<?>) view3).setOnItemSelectedListener(null);\n"
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
}
