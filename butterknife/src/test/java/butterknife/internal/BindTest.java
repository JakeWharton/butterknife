package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindTest {
  @Test public void bindingView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "public class Test extends Activity {",
        "    @Bind(1) View thing;",
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
  @Test public void bindingViewTooManyIdsFail() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "public class Test extends Activity {",
        "    @Bind({1, 2}) View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind for a view must only specify one ID. Found: [1, 2]. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void bindingInterface() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "public class Test extends Activity {",
        "    interface TestInterface {}",
        "    @Bind(1) TestInterface thing;",
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
        "import butterknife.Bind;",
        "class Test<T extends TextView> extends Activity {",
        "    @Bind(1) T thing;",
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
        "import butterknife.Bind;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @Bind(1) View thing1;",
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
        "import butterknife.Bind;",
        "public class Test extends Activity {",
        "  @Bind(1) public View thing1;",
        "  @Bind(2) View thing2;",
        "  @Bind(3) protected View thing3;",
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
        "import butterknife.Bind;",
        "public class Test extends Activity {",
        "  @interface Nullable {}",
        "  @Nullable @Bind(1) View view;",
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
        "import butterknife.Bind;",
        "public class Test extends Activity {",
        "  @Bind(1) View view;",
        "}",
        "class TestOne extends Test {",
        "  @Bind(1) View thing;",
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
        "import butterknife.Bind;",
        "public class Test<T> extends Activity {",
        "  @Bind(1) View view;",
        "}",
        "class TestOne extends Test<String> {",
        "  @Bind(1) View thing;",
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
        "import butterknife.Bind;",
        "public class Test {",
        "  @Bind(1) View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@Bind-annotated class incorrectly in Java framework package. (java.test.Test)")
        .in(source).onLine(5);
  }

  @Test public void failsInAndroidPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package android.test;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "public class Test {",
        "  @Bind(1) View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@Bind-annotated class incorrectly in Android framework package. (android.test.Test)")
        .in(source).onLine(5);
  }

  @Test public void failsIfInPrivateClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "public class Test {",
        "  private static class Inner {",
        "    @Bind(1) View thing;",
        "  }",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@Bind fields may not be contained in private classes. (test.Test.Inner.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.Bind;",
        "public class Test extends Activity {",
        "  @Bind(1) String thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind fields must extend from View or be an interface. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfInInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "public interface Test {",
        "    @Bind(1) View thing = null;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@Bind fields may only be contained in classes. (test.Test.thing)")
        .in(source).onLine(4);
  }

  @Test public void failsIfPrivate() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "public class Test extends Activity {",
        "    @Bind(1) private View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind fields must not be private or static. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfStatic() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "public class Test extends Activity {",
        "    @Bind(1) static View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind fields must not be private or static. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void duplicateBindingFails() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "public class Test extends Activity {",
        "    @Bind(1) View thing1;",
        "    @Bind(1) View thing2;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "Attempt to use @Bind for an already bound ID 1 on 'thing1'. (test.Test.thing2)")
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
  @Test public void bindingArray() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "public class Test extends Activity {",
        "    @Bind({1, 2, 3}) View[] thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinding",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.ViewBinder;",
            "public class Test$$ViewBinder<T extends test.Test> implements ViewBinder<T> {",
            "  @Override public void bind(final Finder finder, final T target, Object source) {",
            "    View view;",
            "    target.thing = Finder.arrayOf(",
            "        finder.<android.view.View>findRequiredView(source, 1, \"field 'thing'\"),",
            "        finder.<android.view.View>findRequiredView(source, 2, \"field 'thing'\"),",
            "        finder.<android.view.View>findRequiredView(source, 3, \"field 'thing'\")",
            "    );",
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

  @Test public void bindingArrayWithGenerics() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "public class Test<T extends View> extends Activity {",
        "    @Bind({1, 2, 3}) T[] thing;",
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
            "    target.thing = Finder.arrayOf(",
            "        finder.<android.view.View>findRequiredView(source, 1, \"field 'thing'\"),",
            "        finder.<android.view.View>findRequiredView(source, 2, \"field 'thing'\"),",
            "        finder.<android.view.View>findRequiredView(source, 3, \"field 'thing'\")",
            "    );",
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

  @Test public void bindingArrayWithCast() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.widget.TextView;",
        "import butterknife.Bind;",
        "public class Test extends Activity {",
        "    @Bind({1, 2, 3}) TextView[] thing;",
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
            "    target.thing = Finder.arrayOf(",
            "        finder.<android.widget.TextView>findRequiredView(source, 1, \"field 'thing'\"),",
            "        finder.<android.widget.TextView>findRequiredView(source, 2, \"field 'thing'\"),",
            "        finder.<android.widget.TextView>findRequiredView(source, 3, \"field 'thing'\")",
            "    );",
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

  @Test public void bindingList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "    @Bind({1, 2, 3}) List<View> thing;",
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
            "    target.thing = Finder.listOf(",
            "        finder.<android.view.View>findRequiredView(source, 1, \"field 'thing'\"),",
            "        finder.<android.view.View>findRequiredView(source, 2, \"field 'thing'\"),",
            "        finder.<android.view.View>findRequiredView(source, 3, \"field 'thing'\")",
            "    );",
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

  @Test public void bindingListOfInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.Bind;",
        "import java.util.List;",
        "public class Test {",
        "    interface TestInterface {}",
        "    @Bind({1, 2, 3}) List<TestInterface> thing;",
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
            "    target.thing = Finder.listOf(",
            "        finder.<test.Test.TestInterface>findRequiredView(source, 1, \"field 'thing'\"),",
            "        finder.<test.Test.TestInterface>findRequiredView(source, 2, \"field 'thing'\"),",
            "        finder.<test.Test.TestInterface>findRequiredView(source, 3, \"field 'thing'\")",
            "    );",
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

  @Test public void bindingListWithGenerics() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "import java.util.List;",
        "public class Test<T extends View> extends Activity {",
        "    @Bind({1, 2, 3}) List<T> thing;",
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
            "    target.thing = Finder.listOf(",
            "        finder.<android.view.View>findRequiredView(source, 1, \"field 'thing'\"),",
            "        finder.<android.view.View>findRequiredView(source, 2, \"field 'thing'\"),",
            "        finder.<android.view.View>findRequiredView(source, 3, \"field 'thing'\")",
            "    );",
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

  @Test public void nullableList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "    @interface Nullable {}",
        "    @Nullable @Bind({1, 2, 3}) List<View> thing;",
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
            "    target.thing = Finder.listOf(",
            "        finder.<android.view.View>findOptionalView(source, 1, \"field 'thing'\"),",
            "        finder.<android.view.View>findOptionalView(source, 2, \"field 'thing'\"),",
            "        finder.<android.view.View>findOptionalView(source, 3, \"field 'thing'\")",
            "    );",
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

  @Test public void failsIfNoIds() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "import java.util.List;",
        "public class Test {",
        "  @Bind({}) List<View> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind must specify at least one ID. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfNoGenericType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import butterknife.Bind;",
        "import java.util.List;",
        "public class Test {",
        "  @Bind(1) List thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind List must have a generic component. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfUnsupportedCollection() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "import java.util.Deque;",
        "public class Test {",
        "  @Bind(1) Deque<View> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind must be a List or array. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfGenericNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.Bind;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "  @Bind(1) List<String> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind List or array type must extend from View or be an interface. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfArrayNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.Bind;",
        "public class Test extends Activity {",
        "  @Bind(1) String[] thing;",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind List or array type must extend from View or be an interface. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfContainsDuplicateIds() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.Bind;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "    @Bind({1, 1}) List<View> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@Bind annotation contains duplicate ID 1. (test.Test.thing)")
        .in(source).onLine(7);
  }
}
