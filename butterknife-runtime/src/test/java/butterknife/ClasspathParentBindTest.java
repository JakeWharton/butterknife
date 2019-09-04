package butterknife;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static java.nio.charset.StandardCharsets.UTF_8;

/** Tests binding generation when superclasses are from classpath.  */
public class ClasspathParentBindTest {
  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  @Test
  public void parentBindingInClasspath() throws IOException {
    JavaFileObject baseClassSource = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "import butterknife.OnClick;\n"
        + "import butterknife.OnLongClick;\n"
        + "public class Test {\n"
        + "  @BindView(1) View view;\n"
        + "  @BindView(2) View view2;\n"
        + "  @OnClick(1) void doStuff() {}\n"
        + "  @OnLongClick(1) boolean doMoreStuff() { return false; }\n"
        + "}"
    );

    JavaFileObject subClassSource = JavaFileObjects.forSourceString("test.SubClass", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class SubClass extends Test {\n"
        + "  @BindView(3) View view3;\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/SubClass_ViewBinding", ""
        + "// Generated code from Butter Knife. Do not modify!\n"
        + "package test;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class SubClass_ViewBinding extends Test_ViewBinding {\n"
        + "  private SubClass target;\n"
        + "  @UiThread\n"
        + "  public SubClass_ViewBinding(SubClass target, View source) {\n"
        + "    super(target, source);\n"
        + "    this.target = target;\n"
        + "    target.view3 = Utils.findRequiredView(source, 3, \"field 'view3'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    SubClass target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n"
        + "    target.view3 = null;\n"
        + "    super.unbind();\n"
        + "  }\n"
        + "}"
    );

    File classesOut = tmp.newFolder("classes-output");
    File sourcesOut = tmp.newFolder("sources-output");
    compileSources(classesOut, sourcesOut, baseClassSource);

    try (URLClassLoader compilationClasspath = new URLClassLoader(
        new URL[]{classesOut.toURI().toURL()}, this.getClass().getClassLoader())) {
      assertAbout(javaSource()).that(subClassSource)
          .withCompilerOptions("-Xlint:-processing")
          .withClasspathFrom(compilationClasspath)
          .processedWith(new ButterKnifeProcessor())
          .compilesWithoutWarnings()
          .and()
          .generatesSources(bindingSource);
    }
  }

  @Test
  public void indirectViewRequiredInConstructor() throws IOException {
    JavaFileObject classpathClass = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "import butterknife.OnClick;\n"
        + "import butterknife.OnLongClick;\n"
        + "public class Test {\n"
        + "  @BindView(1) View view;\n"
        + "  @BindView(2) View view2;\n"
        + "  @OnClick(1) void doStuff() {}\n"
        + "  @OnLongClick(1) boolean doMoreStuff() { return false; }\n"
        + "}"
    );

    JavaFileObject subclassInClasspath = JavaFileObjects.forSourceString("test.SubClassTest", ""
        + "package test;\n"
        + "import butterknife.BindFloat;\n"
        + "public class SubClassTest extends Test{\n"
        + "  @BindFloat(1) float value;\n"
        + "}"
    );

    JavaFileObject toProcessSource = JavaFileObjects.forSourceString("test.ToProcess", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "public class ToProcess extends SubClassTest {\n"
        + "  @BindView(3) View view3;\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/ToProcess_ViewBinding", ""
        + "// Generated code from Butter Knife. Do not modify!\n"
        + "package test;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Override;\n"
        + "public class ToProcess_ViewBinding extends SubClassTest_ViewBinding {\n"
        + "  private ToProcess target;\n"
        + "  @UiThread\n"
        + "  public ToProcess_ViewBinding(ToProcess target, View source) {\n"
        + "    super(target, source);\n"
        + "    this.target = target;\n"
        + "    target.view3 = Utils.findRequiredView(source, 3, \"field 'view3'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    ToProcess target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    this.target = null;\n"
        + "    target.view3 = null;\n"
        + "    super.unbind();\n"
        + "  }\n"
        + "}"
    );

    File classesOut = tmp.newFolder("classes-output");
    File sourcesOut = tmp.newFolder("sources-output");
    compileSources(classesOut, sourcesOut, classpathClass, subclassInClasspath);

    try (URLClassLoader compilationClasspath = new URLClassLoader(
        new URL[]{classesOut.toURI().toURL()}, this.getClass().getClassLoader())) {
      assertAbout(javaSource()).that(toProcessSource)
          .withCompilerOptions("-Xlint:-processing")
          .withClasspathFrom(compilationClasspath)
          .processedWith(new ButterKnifeProcessor())
          .compilesWithoutWarnings()
          .and()
          .generatesSources(bindingSource);
    }
  }

  @Test
  public void viewNotRequiredInConstructor() throws IOException {
    JavaFileObject baseClass = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import butterknife.BindFloat;\n"
        + "public class Test {\n"
        + "  @BindFloat(1) float one;\n"
        + "}"
    );

    JavaFileObject subClassSource = JavaFileObjects.forSourceString("test.SubClass", ""
        + "package test;\n"
        + "import butterknife.BindFloat;\n"
        + "public class SubClass extends Test {\n"
        + "  @BindFloat(2) float two;\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/SubClass_ViewBinding", ""
        + "// Generated code from Butter Knife. Do not modify!\n"
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.view.View;\n"
        + "import androidx.annotation.UiThread;\n"
        + "import butterknife.internal.Utils;\n"
        + "import java.lang.Deprecated;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class SubClass_ViewBinding extends Test_ViewBinding {\n"
        + "  /**\n"
        + "   * @deprecated Use {@link #SubClass_ViewBinding(SubClass, Context)} for direct creation.\n"
        + "   *     Only present for runtime invocation through {@code ButterKnife.bind()}.\n"
        + "   */\n"
        + "  @Deprecated\n"
        + "  @UiThread\n"
        + "  public SubClass_ViewBinding(SubClass target, View source) {\n"
        + "    this(target, source.getContext());\n"
        + "  }\n"
        + "  @UiThread\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public SubClass_ViewBinding(SubClass target, Context context) {\n"
        + "    super(target, context);\n"
        + "    target.two = Utils.getFloat(context, 2);\n"
        + "  }\n"
        + "}"
    );

    File classesOut = tmp.newFolder("classes-output");
    File sourcesOut = tmp.newFolder("sources-output");
    compileSources(classesOut, sourcesOut, baseClass);

    try (URLClassLoader compilationClasspath = new URLClassLoader(
        new URL[]{classesOut.toURI().toURL()}, this.getClass().getClassLoader())) {
      assertAbout(javaSource()).that(subClassSource)
          .withCompilerOptions("-Xlint:-processing")
          .withClasspathFrom(compilationClasspath)
          .processedWith(new ButterKnifeProcessor())
          .compilesWithoutWarnings()
          .and()
          .generatesSources(bindingSource);
    }
  }

  private void compileSources(File classesOut, File sourcesOut, JavaFileObject... sources) {
    JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
    try {
      try (StandardJavaFileManager fileManager =
               javaCompiler.getStandardFileManager(null, Locale.getDefault(), UTF_8)) {
        JavaCompiler.CompilationTask javaCompilerTask = javaCompiler.getTask(null,
            fileManager,
            null,
            ImmutableList.of("-d", classesOut.getCanonicalPath(), "-s", sourcesOut.getCanonicalPath()),
            ImmutableList.of(),
            Arrays.asList(sources));
        javaCompilerTask.setProcessors(ImmutableList.of(new ButterKnifeProcessor()));
        javaCompilerTask.call();
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
