package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnPageChangeTest {
  @Test public void pageChange() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnPageChange;",
        "public class Test extends Activity {",
        "  @OnPageChange(1) void doStuff() {}",
        "}"
    ));

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
            "    ((android.support.v4.view.ViewPager) view).setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {",
            "      @Override public void onPageSelected(int p0) {",
            "        target.doStuff();",
            "      }",
            "      @Override public void onPageScrolled(int p0, float p1, int p2) {",
            "      }",
            "      @Override public void onPageScrollStateChanged(int p0) {",
            "      }",
            "    });",
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
