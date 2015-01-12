package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/** This augments {@link OnClickTest} with tests that exercise callbacks with parameters. */
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
            "import butterknife.ButterKnife.Injector;",
            "public class Test$$ViewInjector<T extends test.Test> implements Injector<T> {",
            "  @Override public void inject(final Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((android.widget.AdapterView<?>) view).setOnItemClickListener(",
            "      new android.widget.AdapterView.OnItemClickListener() {",
            "        @Override public void onItemClick(",
            "            android.widget.AdapterView<?> p0, android.view.View p1, int p2, long p3) {",
            "          target.doStuff();",
            "        }",
            "      });",
            "  }",
            "  @Override public void reset(T target) {",
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
            "import butterknife.ButterKnife.Injector;",
            "public class Test$$ViewInjector<T extends test.Test> implements Injector<T> {",
            "  @Override public void inject(final Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((android.widget.AdapterView<?>) view).setOnItemClickListener(",
            "      new android.widget.AdapterView.OnItemClickListener() {",
            "        @Override public void onItemClick(",
            "            android.widget.AdapterView<?> p0, android.view.View p1, int p2, long p3) {",
            "          target.doStuff(p0, p1, p2, p3);",
            "        }",
            "      });",
            "  }",
            "  @Override public void reset(T target) {",
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
            "import butterknife.ButterKnife.Injector;",
            "public class Test$$ViewInjector<T extends test.Test> implements Injector<T> {",
            "  @Override public void inject(final Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((android.widget.AdapterView<?>) view).setOnItemClickListener(",
            "      new android.widget.AdapterView.OnItemClickListener() {",
            "        @Override public void onItemClick(",
            "            android.widget.AdapterView<?> p0, android.view.View p1, int p2, long p3) {",
            "          target.doStuff(finder.<android.widget.ListView>castParam(p0, \"onItemClick\", 0, \"doStuff\", 0), p2);",
            "        }",
            "      });",
            "  }",
            "  @Override public void reset(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void onClickInjectionWithParameterSubsetAndGenerics() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import android.widget.ListView;",
        "import butterknife.OnItemClick;",
        "public class Test<T extends ListView> extends Activity {",
        "  @OnItemClick(1) void doStuff(",
        "    T parent,",
        "    int position",
        "  ) {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.Injector;",
            "public class Test$$ViewInjector<T extends test.Test> implements Injector<T> {",
            "  @Override public void inject(final Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((android.widget.AdapterView<?>) view).setOnItemClickListener(",
            "      new android.widget.AdapterView.OnItemClickListener() {",
            "        @Override public void onItemClick(",
            "            android.widget.AdapterView<?> p0, android.view.View p1, int p2, long p3) {",
            "          target.doStuff(finder.<android.widget.ListView>castParam(p0, \"onItemClick\", 0, \"doStuff\", 0), p2);",
            "        }",
            "      });",
            "  }",
            "  @Override public void reset(T target) {",
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
        "import android.widget.ListView;",
        "import butterknife.OnItemClick;",
        "public class Test extends ListView {",
        "  @OnItemClick void doStuff() {}",
        "  public Test(Context context) {",
        "    super(context);",
        "  }",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.Injector;",
            "public class Test$$ViewInjector<T extends test.Test> implements Injector<T> {",
            "  @Override public void inject(final Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = target;",
            "    ((android.widget.AdapterView<?>) view).setOnItemClickListener(",
            "      new android.widget.AdapterView.OnItemClickListener() {",
            "        @Override public void onItemClick(",
            "          android.widget.AdapterView<?> p0,",
            "          android.view.View p1,",
            "          int p2,",
            "          long p3",
            "        ) {",
            "          target.doStuff();",
            "        }",
            "      });",
            "  }",
            "  @Override public void reset(T target) {",
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
        "import butterknife.OnItemClick;",
        "public class Test extends Activity {",
        "  @OnItemClick({1, -1}) void doStuff() {}",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining("@OnItemClick annotation contains invalid ID -1. (test.Test.doStuff)")
        .in(source).onLine(6);
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
            "Unable to match @OnItemClick method arguments. (test.Test.doStuff)",
            "  ",
            "    Parameter #1: android.widget.AdapterView<?>",
            "      matched listener parameter #1: android.widget.AdapterView<?>",
            "  ",
            "    Parameter #2: android.view.View",
            "      matched listener parameter #2: android.view.View",
            "  ",
            "    Parameter #3: android.view.View",
            "      did not match any listener parameters",
            "  ",
            "  Methods may have up to 4 parameter(s):",
            "  ",
            "    android.widget.AdapterView<?>",
            "    android.view.View",
            "    int",
            "    long",
            "  ",
            "  These may be listed in any order but will be searched for from top to bottom."))
        .in(source).onLine(7);
  }
}
