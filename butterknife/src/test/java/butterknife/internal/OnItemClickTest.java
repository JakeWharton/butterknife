package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

public class OnItemClickTest {
  @Test public void onClickInjection() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnItemClick;",
        "public class Test extends Activity {",
        "  @OnItemClick(1) void doStuff() {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source) {",
            "    View view;",
            "    view = finder.findById(source, 1);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '1' for method 'doStuff' was not found. If this view is optional add '@Optional' annotation.\");",
            "    }",
            "    ((android.widget.AdapterView<?>) view).setOnItemClickListener(",
            "      new android.widget.AdapterView.OnItemClickListener() {",
            "        @Override public void onItemClick(",
            "            android.widget.AdapterView<?> parent, View view, int position, long id) {",
            "          target.doStuff();",
            "        }",
            "      });",
            "  }",
            "  public static void reset(test.Test target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void onClickInjectionWithParameters() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import android.widget.AdapterView;",
        "import butterknife.OnItemClick;",
        "public class Test extends Activity {",
        "  @OnItemClick(1) void doStuff(",
        "    AdapterView<?> parent,",
        "    View view,",
        "    int position,",
        "    long id",
        "  ) {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source) {",
            "    View view;",
            "    view = finder.findById(source, 1);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '1' for method 'doStuff' was not found. If this view is optional add '@Optional' annotation.\");",
            "    }",
            "    ((android.widget.AdapterView<?>) view).setOnItemClickListener(",
            "      new android.widget.AdapterView.OnItemClickListener() {",
            "        @Override public void onItemClick(",
            "            android.widget.AdapterView<?> parent, View view, int position, long id) {",
            "          target.doStuff(parent, view, position, id);",
            "        }",
            "      });",
            "  }",
            "  public static void reset(test.Test target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void onClickInjectionWithParameterSubset() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import android.widget.ListView;",
        "import butterknife.OnItemClick;",
        "public class Test extends Activity {",
        "  @OnItemClick(1) void doStuff(",
        "    ListView parent,",
        "    int position",
        "  ) {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source) {",
            "    View view;",
            "    view = finder.findById(source, 1);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '1' for method 'doStuff' was not found. If this view is optional add '@Optional' annotation.\");",
            "    }",
            "    ((android.widget.AdapterView<?>) view).setOnItemClickListener(",
            "      new android.widget.AdapterView.OnItemClickListener() {",
            "        @Override public void onItemClick(",
            "            android.widget.AdapterView<?> parent, View view, int position, long id) {",
            "          target.doStuff((android.widget.ListView) parent, position);",
            "        }",
            "      });",
            "  }",
            "  public static void reset(test.Test target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void failsWithInvalidParameterConfiguration() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import android.widget.AdapterView;",
        "import butterknife.OnItemClick;",
        "public class Test extends Activity {",
        "  @OnItemClick(1) void doStuff(",
        "    AdapterView<?> parent,",
        "    View view,",
        "    View whatIsThis",
        "  ) {}",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(Joiner.on('\n').join(
            "Unable to match @OnItemClick method arguments.",
            "",
            "  Parameter #0 (android.widget.AdapterView<?>) matched listener parameter #0",
            "  Parameter #1 (android.view.View) matched listener parameter #1",
            "  Parameter #2 did not match",
            "",
            "@OnItemClick methods may only have up to four parameters (test.Test.doStuff):",
            "",
            "  AdapterView (parent view),",
            "  View (clicked view),",
            "  int (position),",
            "  long (id),",
            "",
            "These may be listed in any order but will be search for from top to bottom."))
        .in(source).onLine(7);
  }
}
