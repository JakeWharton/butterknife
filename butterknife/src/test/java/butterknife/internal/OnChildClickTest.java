package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/** This augments {@link OnClickTest} with tests that exercise callbacks with parameters. */
public class OnChildClickTest {
  @Test public void onClickInjection() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnChildClick;",
        "public class Test extends Activity {",
        "  @OnChildClick(1) boolean doStuff() {return false;}",
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
            "    ((android.widget.ExpandableListView) view).setOnChildClickListener(",
            "      new android.widget.ExpandableListView.OnChildClickListener() {",
            "        @Override public boolean onChildClick(",
            "            android.widget.ExpandableListView p0, android.view.View p1, int p2, int p3, long p4) {",
            "          return target.doStuff();",
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

  @Test public void onClickInjectionWithParameters() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import android.widget.ExpandableListView;",
        "import butterknife.OnChildClick;",
        "public class Test extends Activity {",
        "  @OnChildClick(1) boolean doStuff(",
        "    ExpandableListView parent,",
        "    View view,",
        "    int groupPosition,",
        "    int childPosition,",
        "    long id",
        "  ) {return false;}",
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
            "    ((android.widget.ExpandableListView) view).setOnChildClickListener(",
            "      new android.widget.ExpandableListView.OnChildClickListener() {",
            "        @Override public boolean onChildClick(",
            "            android.widget.ExpandableListView p0, android.view.View p1, int p2, int p3, long p4) {",
            "          return target.doStuff(p0, p1, p2, p3, p4);",
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

  @Test public void onClickInjectionWithParameterSubset() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import android.widget.ExpandableListView;",
        "import butterknife.OnChildClick;",
        "public class Test extends Activity {",
        "  @OnChildClick(1) boolean doStuff(",
        "    ExpandableListView parent,",
        "    int position",
        "  ) {return false;}",
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
            "    ((android.widget.ExpandableListView) view).setOnChildClickListener(",
            "      new android.widget.ExpandableListView.OnChildClickListener() {",
            "        @Override public boolean onChildClick(",
            "            android.widget.ExpandableListView p0, android.view.View p1, int p2, int p3, long p4) {",
            "          return target.doStuff(p0, p2);",
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

  @Test public void onClickRootViewInjection() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.content.Context;",
        "import android.widget.ExpandableListView;",
        "import butterknife.OnChildClick;",
        "public class Test extends ExpandableListView {",
        "  @OnChildClick boolean doStuff() {return false;}",
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
            "    ((android.widget.ExpandableListView) view).setOnChildClickListener(",
            "      new android.widget.ExpandableListView.OnChildClickListener() {",
            "        @Override public boolean onChildClick(",
            "          android.widget.ExpandableListView p0,",
            "          android.view.View p1,",
            "          int p2,",
            "          int p3,",
            "          long p4",
            "        ) {",
            "          return target.doStuff();",
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
        "import butterknife.OnChildClick;",
        "public class Test extends Activity {",
        "  @OnChildClick({1, -1}) boolean doStuff() {return false;}",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining("@OnChildClick annotation contains invalid ID -1. (test.Test.doStuff)")
        .in(source).onLine(6);
  }

  @Test public void failsWithInvalidParameterConfiguration() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import android.widget.ExpandableListView;",
        "import butterknife.OnChildClick;",
        "public class Test extends Activity {",
        "  @OnChildClick(1) boolean doStuff(",
        "    ExpandableListView parent,",
        "    View view,",
        "    View whatIsThis",
        "  ) {return false;}",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(Joiner.on('\n').join(
            "Unable to match @OnChildClick method arguments. (test.Test.doStuff)",
            "  ",
            "    Parameter #1: android.widget.ExpandableListView",
            "      matched listener parameter #1: android.widget.ExpandableListView",
            "  ",
            "    Parameter #2: android.view.View",
            "      matched listener parameter #2: android.view.View",
            "  ",
            "    Parameter #3: android.view.View",
            "      did not match any listener parameters",
            "  ",
            "  Methods may have up to 5 parameter(s):",
            "  ",
            "    android.widget.ExpandableListView",
            "    android.view.View",
            "    int",
            "    int",
            "    long",
            "  ",
            "  These may be listed in any order but will be searched for from top to bottom."))
        .in(source).onLine(7);
  }
}
