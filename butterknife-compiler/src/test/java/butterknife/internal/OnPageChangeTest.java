package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

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

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.support.v4.view.ViewPager;",
            "import android.view.View;",
            "import butterknife.ButterKnife;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterKnife.ViewBinder<T> {",
            "  @Override public void bind(final ButterKnife.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((ViewPager) view).setOnPageChangeListener(new ViewPager.OnPageChangeListener() {",
            "      @Override public void onPageSelected(int p0) {",
            "        target.doStuff();",
            "      }",
            "      @Override public void onPageScrolled(int p0, float p1, int p2) {",
            "      }",
            "      @Override public void onPageScrollStateChanged(int p0) {",
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
