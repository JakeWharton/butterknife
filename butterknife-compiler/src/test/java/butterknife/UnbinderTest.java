package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class UnbinderTest {
  @Test public void bindingUnbinder() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n')
        .join(
            "package test;",
            "import android.support.v4.app.Fragment;",
            "import butterknife.ButterKnife;",
            "import butterknife.OnClick;",
            "import butterknife.Unbinder;",
            "public class Test extends Fragment {",
            "  @Unbinder ButterKnife.Unbinder unbinder;",
            "  @OnClick(1) void doStuff() {",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.internal.DebouncingOnClickListener;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.ViewBinder;",
            "import java.lang.IllegalStateException;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
            "  @Override public void bind(final Finder finder, final T target, Object source) {",
            "    Unbinder unbinder = new Unbinder(target);",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"method 'doStuff'\");",
            "    unbinder.view1 = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.doStuff();",
            "      }",
            "    });",
            "    target.unbinder = unbinder;",
            "  }",
            "  private static final class Unbinder implements butterknife.ButterKnife.Unbinder {",
            "    private Test target;",
            "    View view1;",
            "    Unbinder(Test target) {",
            "      this.target = target;",
            "    }",
            "    @Override public void unbind() {",
            "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");",
            "      view1.setOnClickListener(null);",
            "      target.unbinder = null;",
            "      target = null;",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void failWhenMultipleUnbinders() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n')
        .join(
            "package test;",
            "import android.support.v4.app.Fragment;",
            "import butterknife.ButterKnife;",
            "import butterknife.Unbinder;",
            "public class Test extends Fragment {",
            "  @Unbinder ButterKnife.Unbinder unbinder1;",
            "  @Unbinder ButterKnife.Unbinder unbinder2;",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "Only one filed should be annotated with @Unbinder. (test.Test.unbinder2)")
        .in(source).onLine(7);
  }

  @Test public void failOnWrongUnbinderType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n')
        .join(
            "package test;",
            "import android.support.v4.app.Fragment;",
            "import butterknife.Unbinder;",
            "public class Test extends Fragment {",
            "  @Unbinder Object unbinder;",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@Unbinder filed must be of type ButterKnife.Unbinder. (test.Test.unbinder)")
        .in(source).onLine(5);
  }

  @Test public void multipleBindings() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n')
        .join(
            "package test;",
            "import android.support.v4.app.Fragment;",
            "import android.view.View;",
            "import butterknife.Bind;",
            "import butterknife.ButterKnife;",
            "import butterknife.OnClick;",
            "import butterknife.OnLongClick;",
            "import butterknife.Unbinder;",
            "public class Test extends Fragment {",
            "  @Unbinder ButterKnife.Unbinder unbinder;",
            "  @Bind(1) View view;",
            "  @Bind(2) View view2;",
            "  @OnClick(1) void doStuff() {",
            "  }",
            "  @OnLongClick(1) boolean doMoreStuff() { return false; }",
            "}"
        ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.internal.DebouncingOnClickListener;",
            "import butterknife.internal.Finder;",
            "import butterknife.internal.ViewBinder;",
            "import java.lang.IllegalStateException;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
            "  @Override public void bind(final Finder finder, final T target, Object source) {",
            "    Unbinder unbinder = new Unbinder(target);",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"field 'view', method 'doStuff', and method 'doMoreStuff'\");",
            "    target.view = view;",
            "    unbinder.view1 = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.doStuff();",
            "      }",
            "    });",
            "    view.setOnLongClickListener(new View.OnLongClickListener() {",
            "      @Override public boolean onLongClick(View p0) {",
            "        return target.doMoreStuff();",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, 2, \"field 'view2'\");",
            "    target.view2 = view;",
            "    target.unbinder = unbinder;",
            "  }",
            "  private static final class Unbinder implements butterknife.ButterKnife.Unbinder {",
            "    private Test target;",
            "    View view1;",
            "    Unbinder(Test target) {",
            "      this.target = target;",
            "    }",
            "    @Override public void unbind() {",
            "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");",
            "      view1.setOnClickListener(null);",
            "      view1.setOnLongClickListener(null);",
            "      target.view = null;",
            "      target.view2 = null;",
            "      target.unbinder = null;",
            "      target = null;",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void unbinderRespectsNullable() {
      JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n')
          .join(
              "package test;",
              "import android.support.v4.app.Fragment;",
              "import butterknife.ButterKnife;",
              "import butterknife.OnClick;",
              "import butterknife.Optional;",
              "import butterknife.Unbinder;",
              "public class Test extends Fragment {",
              "  @Unbinder ButterKnife.Unbinder unbinder;",
              "  @Optional @OnClick(1) void doStuff() {",
              "  }",
              "}"
          ));

      JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
          Joiner.on('\n').join(
              "package test;",
              "import android.view.View;",
              "import butterknife.internal.DebouncingOnClickListener;",
              "import butterknife.internal.Finder;",
              "import butterknife.internal.ViewBinder;",
              "import java.lang.IllegalStateException;",
              "import java.lang.Object;",
              "import java.lang.Override;",
              "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {",
              "  @Override public void bind(final Finder finder, final T target, Object source) {",
              "    Unbinder unbinder = new Unbinder(target);",
              "    View view;",
              "    view = finder.findOptionalView(source, 1, null);",
              "    if (view != null) {",
              "      unbinder.view1 = view;",
              "      view.setOnClickListener(new DebouncingOnClickListener() {",
              "        @Override public void doClick(View p0) {",
              "          target.doStuff();",
              "        }",
              "      });",
              "    }",
              "    target.unbinder = unbinder;",
              "  }",
              "  private static final class Unbinder implements butterknife.ButterKnife.Unbinder {",
              "    private Test target;",
              "    View view1;",
              "    Unbinder(Test target) {",
              "      this.target = target;",
              "    }",
              "    @Override public void unbind() {",
              "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");",
              "      if (view1 != null) {",
              "        view1.setOnClickListener(null);",
              "      }",
              "      target.unbinder = null;",
              "      target = null;",
              "    }",
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
