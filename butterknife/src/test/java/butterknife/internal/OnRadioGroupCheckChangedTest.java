package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnRadioGroupCheckChangedTest {
    @Test
    public void checkedChanged() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
                "package test;",
                "import android.app.Activity;",
                "import butterknife.OnRadioGroupCheckedChanged;",
                "public class Test extends Activity {",
                "  @OnRadioGroupCheckedChanged(1) void doStuff() {}",
                "}"
        ));

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
                        "    ((android.widget.RadioGroup) view).setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {",
                        "      @Override public void onCheckedChanged(android.widget.RadioGroup p0, int p1) {",
                        "        target.doStuff();",
                        "      }",
                        "    });",
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
}
