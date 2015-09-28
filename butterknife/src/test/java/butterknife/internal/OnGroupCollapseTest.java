package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnGroupCollapseTest {
  @Test public void onCollapseInjection() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnGroupCollapse;",
        "public class Test extends Activity {",
        "  @OnGroupCollapse(1) void doStuff() {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.ViewBinder;",
            "public class Test$$ViewBinder<T extends test.Test> implements ViewBinder<T> {",
            "  @Override public void bind(final Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((android.widget.ExpandableListView) view).setOnGroupCollapseListener(",
            "      new android.widget.ExpandableListView.OnGroupCollapseListener() {",
            "        @Override public void onGroupCollapse(int p0) {",
            "          target.doStuff();",
            "        }",
            "      });",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void onCollapseInjectionWithParameters() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import android.widget.ExpandableListView;",
        "import butterknife.OnGroupCollapse;",
        "public class Test extends Activity {",
        "  @OnGroupCollapse(1) void doStuff(",
        "    int position",
        "  ) {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.ViewBinder;",
            "public class Test$$ViewBinder<T extends test.Test> implements ViewBinder<T> {",
            "  @Override public void bind(final Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((android.widget.ExpandableListView) view).setOnGroupCollapseListener(",
            "      new android.widget.ExpandableListView.OnGroupCollapseListener() {",
            "        @Override public void onGroupCollapse(int p0) {",
            "          target.doStuff(p0);",
            "        }",
            "      });",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void onCollapseRootViewInjection() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.content.Context;",
        "import android.widget.ExpandableListView;",
        "import butterknife.OnGroupCollapse;",
        "public class Test extends ExpandableListView {",
        "  @OnGroupCollapse void doStuff() {}",
        "  public Test(Context context) {",
        "    super(context);",
        "  }",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.ViewBinder;",
            "public class Test$$ViewBinder<T extends test.Test> implements ViewBinder<T> {",
            "  @Override public void bind(final Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = target;",
            "    ((android.widget.ExpandableListView) view).setOnGroupCollapseListener(",
            "      new android.widget.ExpandableListView.OnGroupCollapseListener() {",
            "        @Override public void onGroupCollapse(int p0) {",
            "          target.doStuff();",
            "        }",
            "      });",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void failsWithInvalidId() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.content.Context;",
        "import android.app.Activity;",
        "import butterknife.OnGroupCollapse;",
        "public class Test extends Activity {",
        "  @OnGroupCollapse({1, -1}) void doStuff() {}",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining("@OnGroupCollapse annotation contains invalid ID -1. (test.Test.doStuff)")
        .in(source).onLine(6);
  }

  @Test public void failsWithInvalidParameterConfiguration() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import android.widget.ExpandableListView;",
        "import butterknife.OnGroupCollapse;",
        "public class Test extends Activity {",
        "  @OnGroupCollapse(1) void doStuff(View whatIsThis) {}",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(Joiner.on('\n').join(
            "Unable to match @OnGroupCollapse method arguments. (test.Test.doStuff)",
            "  ",
            "    Parameter #1: android.view.View",
            "      did not match any listener parameters",
            "  ",
            "  Methods may have up to 1 parameter(s):",
            "  ",
            "    int",
            "  ",
            "  These may be listed in any order but will be searched for from top to bottom."))
        .in(source).onLine(7);
  }
}
