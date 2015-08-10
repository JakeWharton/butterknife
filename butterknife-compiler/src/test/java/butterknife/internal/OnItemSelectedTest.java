package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

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

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.AdapterView;",
            "import butterknife.ButterKnife;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterKnife.ViewBinder<T> {",
            "  @Override public void bind(final ButterKnife.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {",
            "      @Override public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {",
            "        target.doStuff();",
            "      }",
            "      @Override public void onNothingSelected(AdapterView<?> p0) {",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
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

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.AdapterView;",
            "import butterknife.ButterKnife;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterKnife.ViewBinder<T> {",
            "  @Override public void bind(final ButterKnife.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {",
            "      @Override public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {",
            "      }",
            "      @Override public void onNothingSelected(AdapterView<?> p0) {",
            "        target.doStuff();",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
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

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.AdapterView;",
            "import butterknife.ButterKnife;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterKnife.ViewBinder<T> {",
            "  @Override public void bind(final ButterKnife.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'onItemSelected' and method 'onNothingSelected'\");",
            "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {",
            "      @Override public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {",
            "        target.onItemSelected();",
            "      }",
            "      @Override public void onNothingSelected(AdapterView<?> p0) {",
            "        target.onNothingSelected();",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
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

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.AdapterView;",
            "import butterknife.ButterKnife;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterKnife.ViewBinder<T> {",
            "  @Override public void bind(final ButterKnife.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'onItemSelected' and method 'onNothingSelected'\");",
            "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {",
            "      @Override public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {",
            "        target.onItemSelected();",
            "      }",
            "      @Override public void onNothingSelected(AdapterView<?> p0) {",
            "        target.onNothingSelected();",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, 2, \"method 'onItemSelected'\");",
            "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {",
            "      @Override public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {",
            "        target.onItemSelected();",
            "      }",
            "      @Override public void onNothingSelected(AdapterView<?> p0) {",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, 3, \"method 'onNothingSelected'\");",
            "    ((AdapterView<?>) view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {",
            "      @Override public void onItemSelected(AdapterView<?> p0, View p1, int p2, long p3) {",
            "      }",
            "      @Override public void onNothingSelected(AdapterView<?> p0) {",
            "        target.onNothingSelected();",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
