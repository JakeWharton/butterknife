package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindViewTest {
  @Test public void bindingView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "    @BindView(1) View thing;",
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
            "    view = finder.findRequiredView(source, 1, \"field 'thing'\");",
            "    target.thing = view;",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void bindingInterface() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "    interface TestInterface {}",
        "    @BindView(1) TestInterface thing;",
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
            "    view = finder.findRequiredView(source, 1, \"field 'thing'\");",
            "    target.thing = finder.castView(view, 1, \"field 'thing'\");",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void genericType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.widget.EditText;",
        "import android.widget.TextView;",
        "import butterknife.BindView;",
        "class Test<T extends TextView> extends Activity {",
        "    @BindView(1) T thing;",
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
            "    view = finder.findRequiredView(source, 1, \"field 'thing'\");",
            "    target.thing = finder.castView(view, 1, \"field 'thing'\");",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void oneFindPerId() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @BindView(1) View thing1;",
        "  @OnClick(1) void doStuff() {}",
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
            "    view = finder.findRequiredView(source, 1, \"field 'thing1' and method 'doStuff'\");",
            "    target.thing1 = view;",
            "    view.setOnClickListener(",
            "      new butterknife.internal.DebouncingOnClickListener() {",
            "        @Override public void doClick(",
            "          android.view.View p0",
            "        ) {",
            "          target.doStuff();",
            "        }",
            "      });",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.thing1 = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void fieldVisibility() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "  @BindView(1) public View thing1;",
        "  @BindView(2) View thing2;",
        "  @BindView(3) protected View thing3;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError();
  }

  @Test public void nullable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "  @interface Nullable {}",
        "  @Nullable @BindView(1) View view;",
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
            "    view = finder.findOptionalView(source, 1, null);",
            "    target.view = view;",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.view = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void superclass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "  @BindView(1) View view;",
        "}",
        "class TestOne extends Test {",
        "  @BindView(1) View thing;",
        "}",
        "class TestTwo extends Test {",
        "}"
    ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.ViewBinder;",
            "public class Test$$ViewBinder<T extends test.Test> implements ViewBinder<T> {",
            "  @Override public void bind(final Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"field 'view'\");",
            "    target.view = view;",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.view = null;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestOne$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "public class TestOne$$ViewBinder<T extends test.TestOne> ",
            "    extends test.Test$$ViewBinder<T> {",
            "  @Override public void bind(final Finder finder, final T target, Object source) {",
            "    super.bind(finder, target, source);",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"field 'thing'\");",
            "    target.thing = view;",
            "  }",
            "  @Override public void unbind(T target) {",
            "    super.unbind(target);",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void genericSuperclass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test<T> extends Activity {",
        "  @BindView(1) View view;",
        "}",
        "class TestOne extends Test<String> {",
        "  @BindView(1) View thing;",
        "}",
        "class TestTwo extends Test<Object> {",
        "}"
    ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.ViewBinder;",
            "public class Test$$ViewBinder<T extends test.Test> implements ViewBinder<T> {",
            "  @Override public void bind(final Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"field 'view'\");",
            "    target.view = view;",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.view = null;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestOne$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "public class TestOne$$ViewBinder<T extends test.TestOne> ",
            "    extends test.Test$$ViewBinder<T> {",
            "  @Override public void bind(final Finder finder, final T target, Object source) {",
            "    super.bind(finder, target, source);",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"field 'thing'\");",
            "    target.thing = view;",
            "  }",
            "  @Override public void unbind(T target) {",
            "    super.unbind(target);",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void failsInJavaPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package java.test;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test {",
        "  @BindView(1) View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindView-annotated class incorrectly in Java framework package. (java.test.Test)")
        .in(source).onLine(5);
  }

  @Test public void failsInAndroidPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package android.test;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test {",
        "  @BindView(1) View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindView-annotated class incorrectly in Android framework package. (android.test.Test)")
        .in(source).onLine(5);
  }

  @Test public void failsIfInPrivateClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test {",
        "  private static class Inner {",
        "    @BindView(1) View thing;",
        "  }",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindView fields may not be contained in private classes. (test.Test.Inner.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "  @BindView(1) String thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindView fields must extend from View or be an interface. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfInInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public interface Test {",
        "    @BindView(1) View thing = null;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindView fields may only be contained in classes. (test.Test.thing)")
        .in(source).onLine(4);
  }

  @Test public void failsIfPrivate() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "    @BindView(1) private View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindView fields must not be private or static. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfStatic() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "    @BindView(1) static View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindView fields must not be private or static. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfBothAnnotations() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "import butterknife.BindViews;",
        "public class Test extends Activity {",
        "    @BindView(1) @BindViews(1) View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "Only one of @BindView and @BindViews is allowed. (test.Test.thing)")
        .in(source).onLine(7);
  }

  @Test public void duplicateBindingFails() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "    @BindView(1) View thing1;",
        "    @BindView(1) View thing2;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "Attempt to use @BindView for an already bound ID 1 on 'thing1'. (test.Test.thing2)")
        .in(source).onLine(7);
  }

  @Test public void failsRootViewBindingWithBadTarget() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.view.View;",
            "import butterknife.OnItemClick;",
            "public class Test extends View {",
            "  @OnItemClick void doStuff() {}",
            "  public Test(Context context) {",
            "    super(context);",
            "  }",
            "}"));

    ASSERT.about(javaSource())
        .that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining((
            "@OnItemClick annotation without an ID may only be used with an object of type "
                + "\"android.widget.AdapterView<?>\" or an interface. (test.Test.doStuff)"))
        .in(source)
        .onLine(6);
  }

  @Test public void failsOptionalRootViewBinding() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.view.View;",
            "import butterknife.OnClick;",
            "public class Test extends View {",
            "  @interface Nullable {}",
            "  @Nullable @OnClick void doStuff() {}",
            "  public Test(Context context) {",
            "    super(context);",
            "  }",
            "}"));

    ASSERT.about(javaSource())
        .that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            ("ID-free binding must not be annotated with @Nullable. (test.Test.doStuff)"))
        .in(source)
        .onLine(7);
  }
}
