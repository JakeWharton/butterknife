package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnTextChangedTest {
  @Test public void textChanged() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnTextChanged;",
        "public class Test extends Activity {",
        "  @OnTextChanged(1) void doStuff() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((android.widget.TextView) view).addTextChangedListener(new butterknife.internal.DebouncingTextWatcher() {",
            "      @Override public void doTextChanged(java.lang.CharSequence p0, int p1, int p2, int p3) {",
            "        target.doStuff();",
            "      }",
            "      @Override public void doBeforeTextChanged(java.lang.CharSequence p0, int p1, int p2, int p3) {",
            "      }",
            "      @Override public void doAfterTextChanged(android.text.Editable p0) {",
            "      }",
            "    });",
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

  @Test public void beforeTextChanged() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnTextChanged;",
        "import static butterknife.OnTextChanged.Callback.BEFORE_TEXT_CHANGED;",
        "public class Test extends Activity {",
        "  @OnTextChanged(value = 1, callback = BEFORE_TEXT_CHANGED) void doStuff() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((android.widget.TextView) view).addTextChangedListener(new butterknife.internal.DebouncingTextWatcher() {",
            "      @Override public void doTextChanged(java.lang.CharSequence p0, int p1, int p2, int p3) {",
            "      }",
            "      @Override public void doBeforeTextChanged(java.lang.CharSequence p0, int p1, int p2, int p3) {",
            "        target.doStuff();",
            "      }",
            "      @Override public void doAfterTextChanged(android.text.Editable p0) {",
            "      }",
            "    });",
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

  @Test public void afterTextChanged() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnTextChanged;",
        "import static butterknife.OnTextChanged.Callback.AFTER_TEXT_CHANGED;",
        "public class Test extends Activity {",
        "  @OnTextChanged(value = 1, callback = AFTER_TEXT_CHANGED) void doStuff() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((android.widget.TextView) view).addTextChangedListener(new butterknife.internal.DebouncingTextWatcher() {",
            "      @Override public void doTextChanged(java.lang.CharSequence p0, int p1, int p2, int p3) {",
            "      }",
            "      @Override public void doBeforeTextChanged(java.lang.CharSequence p0, int p1, int p2, int p3) {",
            "      }",
            "      @Override public void doAfterTextChanged(android.text.Editable p0) {",
            "        target.doStuff();",
            "      }",
            "    });",
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
}
