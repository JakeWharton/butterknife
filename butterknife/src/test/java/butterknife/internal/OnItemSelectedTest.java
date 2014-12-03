package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/** This augments {@link OnClickTest} with tests that exercise callbacks with multiple methods. */
public class OnItemSelectedTest {
  @Test public void defaultMethod() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnItemSelected;",
        "public class Test extends Activity {",
        "  @OnItemSelected(1) void doStuff() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.Injector;",
            "public class Test$$ViewInjector<T extends test.Test> implements Injector<T> {",
            "  @Override public void inject(Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((android.widget.AdapterView<?>) view).setOnItemSelectedListener(",
            "      new android.widget.AdapterView.OnItemSelectedListener() {",
            "        @Override public void onItemSelected(",
            "            android.widget.AdapterView<?> p0, android.view.View p1, int p2, long p3) {",
            "          target.doStuff();",
            "        }",
            "        @Override public void onNothingSelected(",
            "            android.widget.AdapterView<?> p0) {",
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

  @Test public void nonDefaultMethod() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnItemSelected;",
        "import static butterknife.OnItemSelected.Callback.NOTHING_SELECTED;",
        "public class Test extends Activity {",
        "  @OnItemSelected(value = 1, callback = NOTHING_SELECTED)",
        "  void doStuff() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.Injector;",
            "public class Test$$ViewInjector<T extends test.Test> implements Injector<T> {",
            "  @Override public void inject(Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((android.widget.AdapterView<?>) view).setOnItemSelectedListener(",
            "      new android.widget.AdapterView.OnItemSelectedListener() {",
            "        @Override public void onItemSelected(",
            "            android.widget.AdapterView<?> p0, android.view.View p1, int p2, long p3) {",
            "        }",
            "        @Override public void onNothingSelected(",
            "            android.widget.AdapterView<?> p0) {",
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

  @Test public void allMethods() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnItemSelected;",
        "import static butterknife.OnItemSelected.Callback.NOTHING_SELECTED;",
        "public class Test extends Activity {",
        "  @OnItemSelected(1)",
        "  void onItemSelected() {}",
        "  @OnItemSelected(value = 1, callback = NOTHING_SELECTED)",
        "  void onNothingSelected() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.Injector;",
            "public class Test$$ViewInjector<T extends test.Test> implements Injector<T> {",
            "  @Override public void inject(Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'onItemSelected' and method 'onNothingSelected'\");",
            "    ((android.widget.AdapterView<?>) view).setOnItemSelectedListener(",
            "      new android.widget.AdapterView.OnItemSelectedListener() {",
            "        @Override public void onItemSelected(",
            "            android.widget.AdapterView<?> p0, android.view.View p1, int p2, long p3) {",
            "          target.onItemSelected();",
            "        }",
            "        @Override public void onNothingSelected(",
            "            android.widget.AdapterView<?> p0) {",
            "          target.onNothingSelected();",
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

  @Test public void multipleBindingPermutation() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnItemSelected;",
        "import static butterknife.OnItemSelected.Callback.NOTHING_SELECTED;",
        "public class Test extends Activity {",
        "  @OnItemSelected({ 1, 2 })",
        "  void onItemSelected() {}",
        "  @OnItemSelected(value = { 1, 3 }, callback = NOTHING_SELECTED)",
        "  void onNothingSelected() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.Injector;",
            "public class Test$$ViewInjector<T extends test.Test> implements Injector<T> {",
            "  @Override public void inject(Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'onItemSelected' and method 'onNothingSelected'\");",
            "    ((android.widget.AdapterView<?>) view).setOnItemSelectedListener(",
            "      new android.widget.AdapterView.OnItemSelectedListener() {",
            "        @Override public void onItemSelected(",
            "            android.widget.AdapterView<?> p0, android.view.View p1, int p2, long p3) {",
            "          target.onItemSelected();",
            "        }",
            "        @Override public void onNothingSelected(",
            "            android.widget.AdapterView<?> p0) {",
            "          target.onNothingSelected();",
            "        }",
            "      });",
            "    view = finder.findRequiredView(source, 2, \"method 'onItemSelected'\");",
            "    ((android.widget.AdapterView<?>) view).setOnItemSelectedListener(",
            "      new android.widget.AdapterView.OnItemSelectedListener() {",
            "        @Override public void onItemSelected(",
            "            android.widget.AdapterView<?> p0, android.view.View p1, int p2, long p3) {",
            "          target.onItemSelected();",
            "        }",
            "        @Override public void onNothingSelected(",
            "            android.widget.AdapterView<?> p0) {",
            "        }",
            "      });",
            "    view = finder.findRequiredView(source, 3, \"method 'onNothingSelected'\");",
            "    ((android.widget.AdapterView<?>) view).setOnItemSelectedListener(",
            "      new android.widget.AdapterView.OnItemSelectedListener() {",
            "        @Override public void onItemSelected(",
            "            android.widget.AdapterView<?> p0, android.view.View p1, int p2, long p3) {",
            "        }",
            "        @Override public void onNothingSelected(",
            "            android.widget.AdapterView<?> p0) {",
            "          target.onNothingSelected();",
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
}
