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
        + "import android.app.Activity;\n"
        + "import butterknife.OnItemSelected;\n"
        + "public class Test extends Activity {\n"
        + "  @OnItemSelected(1) void doStuff() {}\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new InnerUnbinder<>(target, finder, source);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    private View view1;\n"
        + "    protected InnerUnbinder(final T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      View view;\n"
        + "      view = finder.findRequiredView(source, 1, \"method 'doStuff'\");\n"
        + "      view1 = view;\n"
        + "      ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {\n"
        + "        @Override\n"
        + "        public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "          target.doStuff();\n"
        + "        }\n"
        + "        @Override\n"
        + "        public void onNothingSelected(AdapterView<?> p0) {\n"
        + "        }\n"
        + "      });\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      ((AdapterView<?>) view1).setOnItemSelectedListener(null);\n"
        + "      view1 = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void nonDefaultMethod() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;"
        + "import android.app.Activity;"
        + "import butterknife.OnItemSelected;"
        + "import static butterknife.OnItemSelected.Callback.NOTHING_SELECTED;"
        + "public class Test extends Activity {"
        + "  @OnItemSelected(value = 1, callback = NOTHING_SELECTED)"
        + "  void doStuff() {}"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new InnerUnbinder<>(target, finder, source);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    private View view1;\n"
        + "    protected InnerUnbinder(final T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      View view;\n"
        + "      view = finder.findRequiredView(source, 1, \"method 'doStuff'\");\n"
        + "      view1 = view;\n"
        + "      ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {\n"
        + "        @Override\n"
        + "        public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        }\n"
        + "        @Override\n"
        + "        public void onNothingSelected(AdapterView<?> p0) {\n"
        + "          target.doStuff();\n"
        + "        }\n"
        + "      });\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      ((AdapterView<?>) view1).setOnItemSelectedListener(null);\n"
        + "      view1 = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void allMethods() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;"
        + "import android.app.Activity;"
        + "import butterknife.OnItemSelected;"
        + "import static butterknife.OnItemSelected.Callback.NOTHING_SELECTED;"
        + "public class Test extends Activity {"
        + "  @OnItemSelected(1)"
        + "  void onItemSelected() {}"
        + "  @OnItemSelected(value = 1, callback = NOTHING_SELECTED)"
        + "  void onNothingSelected() {}"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new InnerUnbinder<>(target, finder, source);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    private View view1;\n"
        + "    protected InnerUnbinder(final T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      View view;\n"
        + "      view = finder.findRequiredView(source, 1, \"method 'onItemSelected' and method 'onNothingSelected'\");\n"
        + "      view1 = view;\n"
        + "      ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {\n"
        + "        @Override\n"
        + "        public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "          target.onItemSelected();\n"
        + "        }\n"
        + "        @Override\n"
        + "        public void onNothingSelected(AdapterView<?> p0) {\n"
        + "          target.onNothingSelected();\n"
        + "        }\n"
        + "      });\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      ((AdapterView<?>) view1).setOnItemSelectedListener(null);\n"
        + "      view1 = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void multipleBindingPermutation() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;"
        + "import android.app.Activity;"
        + "import butterknife.OnItemSelected;"
        + "import static butterknife.OnItemSelected.Callback.NOTHING_SELECTED;"
        + "public class Test extends Activity {"
        + "  @OnItemSelected({ 1, 2 })"
        + "  void onItemSelected() {}"
        + "  @OnItemSelected(value = { 1, 3 }, callback = NOTHING_SELECTED)"
        + "  void onNothingSelected() {}"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new InnerUnbinder<>(target, finder, source);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    private View view1;\n"
        + "    private View view2;\n"
        + "    private View view3;\n"
        + "    protected InnerUnbinder(final T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      View view;\n"
        + "      view = finder.findRequiredView(source, 1, \"method 'onItemSelected' and method 'onNothingSelected'\");\n"
        + "      view1 = view;\n"
        + "      ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {\n"
        + "        @Override\n"
        + "        public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "          target.onItemSelected();\n"
        + "        }\n"
        + "        @Override\n"
        + "        public void onNothingSelected(AdapterView<?> p0) {\n"
        + "          target.onNothingSelected();\n"
        + "        }\n"
        + "      });\n"
        + "      view = finder.findRequiredView(source, 2, \"method 'onItemSelected'\");\n"
        + "      view2 = view;\n"
        + "      ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {\n"
        + "        @Override\n"
        + "        public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "          target.onItemSelected();\n"
        + "        }\n"
        + "        @Override\n"
        + "        public void onNothingSelected(AdapterView<?> p0) {\n"
        + "        }\n"
        + "      });\n"
        + "      view = finder.findRequiredView(source, 3, \"method 'onNothingSelected'\");\n"
        + "      view3 = view;\n"
        + "      ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {\n"
        + "        @Override\n"
        + "        public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "        }\n"
        + "        @Override\n"
        + "        public void onNothingSelected(AdapterView<?> p0) {\n"
        + "          target.onNothingSelected();\n"
        + "        }\n"
        + "      });\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      ((AdapterView<?>) view1).setOnItemSelectedListener(null);\n"
        + "      view1 = null;\n"
        + "      ((AdapterView<?>) view2).setOnItemSelectedListener(null);\n"
        + "      view2 = null;\n"
        + "      ((AdapterView<?>) view3).setOnItemSelectedListener(null);\n"
        + "      view3 = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
