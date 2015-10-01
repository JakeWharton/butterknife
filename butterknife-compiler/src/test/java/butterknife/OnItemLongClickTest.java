package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnItemLongClickTest {
  @Test public void itemLongClick() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnItemLongClick;",
        "public class Test extends Activity {",
        "  @OnItemLongClick(1) boolean doStuff() { return false; }",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.AdapterView;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.ViewBinder;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
            "  @Override public void bind(final Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    ((AdapterView<?>) view).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {",
            "      @Override public boolean onItemLongClick(AdapterView<?> p0, View p1, int p2, long p3) {",
            "        return target.doStuff();",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
