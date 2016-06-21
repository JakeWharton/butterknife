package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/** This augments {@link OnClickTest} with tests that exercise callbacks with return types. */
public class OnLongClickTest {
  @Test public void onLongClickBinding() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.OnLongClick;\n"
        + "public class Test extends Activity {\n"
        + "  @OnLongClick(1) boolean doStuff() {\n"
        + "    return true;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binderSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public final class Test_ViewBinder implements ViewBinder<Test> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, Test target, Object source) {\n"
        + "    return new Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding<T extends Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  private View view1;\n"
        + "  public  Test_ViewBinding(final T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");\n"
        + "    view1 = view;\n"
        + "    view.setOnLongClickListener(new View.OnLongClickListener() {\n"
        + "      @Override\n"
        + "      public boolean onLongClick(View p0) {\n"
        + "        return target.doStuff();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    view1.setOnLongClickListener(null);\n"
        + "    view1 = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutWarnings()
        .and()
        .generatesSources(binderSource, bindingSource);
  }

  @Test public void failsIfMissingReturnType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.OnLongClick;\n"
        + "public class Test extends Activity {\n"
        + "  @OnLongClick(1)\n"
        + "  public void doStuff() {\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnLongClick methods must have a 'boolean' return type. (test.Test.doStuff)")
        .in(source).onLine(6);
  }
}
