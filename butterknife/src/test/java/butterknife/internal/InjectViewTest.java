package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import javax.tools.JavaFileObject;

import org.junit.Test;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class InjectViewTest {
  @Test public void injectingView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "public class Test extends Activity {",
        "    @InjectView(1) View thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.InjectUtils;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source, boolean exactMatch) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"field 'thing'\");",
            "    if (view != null) {",
            "      InjectUtils.setMember(target, target.getClass(), \"thing\", view, exactMatch);",
            "    }",
            "  }",
            "  public static void reset(test.Test target, boolean exactMatch) {",
            "    InjectUtils.setMember(target, target.getClass(), \"thing\", null, exactMatch);",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
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
        "import butterknife.InjectView;",
        "class Test<T extends TextView> extends Activity {",
        "    @InjectView(1) T thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.InjectUtils;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source, boolean exactMatch) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"field 'thing'\");",
            "    if (view != null) {",
            "      InjectUtils.setMember(target, target.getClass(), \"thing\", view, exactMatch);",
            "    }",
            "  }",
            "  public static void reset(test.Test target, boolean exactMatch) {",
            "    InjectUtils.setMember(target, target.getClass(), \"thing\", null, exactMatch);",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void oneFindPerId() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @InjectView(1) View thing1;",
        "  @OnClick(1) void doStuff() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.InjectUtils;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source, boolean exactMatch) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"field 'thing1' and method 'doStuff'\");",
            "    if (view != null) {",
            "      InjectUtils.setMember(target, target.getClass(), \"thing1\", view, exactMatch);",
            "      view.setOnClickListener(",
            "        new butterknife.internal.DebouncingOnClickListener() {",
            "          @Override public void doClick(",
            "            android.view.View p0",
            "          ) {",
            "            target.doStuff();",
            "          }",
            "      });",
            "    }",
            "  }",
            "  public static void reset(test.Test target, boolean exactMatch) {",
            "    InjectUtils.setMember(target, target.getClass(), \"thing1\", null, exactMatch);",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void fieldVisibility() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "public class Test extends Activity {",
        "  @InjectView(1) public View thing1;",
        "  @InjectView(2) View thing2;",
        "  @InjectView(3) protected View thing3;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError();
  }

  @Test public void optional() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "import butterknife.Optional;",
        "public class Test extends Activity {",
        "  @Optional @InjectView(1) View view;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.InjectUtils;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source, boolean exactMatch) {",
            "    View view;",
            "    view = finder.findOptionalView(source, 1);",
            "    if (view != null) {",
            "      InjectUtils.setMember(target, target.getClass(), \"view\", view, exactMatch);",
            "    }",
            "  }",
            "  public static void reset(test.Test target, boolean exactMatch) {",
            "    InjectUtils.setMember(target, target.getClass(), \"view\", null, exactMatch);",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void superclass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "import butterknife.Optional;",
        "public class Test extends Activity {",
        "  @InjectView(1) View view;",
        "}",
        "class TestOne extends Test {",
        "  @InjectView(1) View thing;",
        "}",
        "class TestTwo extends Test {",
        "}"
    ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.InjectUtils;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source, boolean exactMatch) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"field 'view'\");",
            "    if (view != null) {",
            "      InjectUtils.setMember(target, target.getClass(), \"view\", view, exactMatch);",
            "    }",
            "  }",
            "  public static void reset(test.Test target, boolean exactMatch) {",
            "    InjectUtils.setMember(target, target.getClass(), \"view\", null, exactMatch);",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestOne$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.InjectUtils;",
            "public class TestOne$$ViewInjector {",
            "  public static void inject(Finder finder, final test.TestOne target, Object source, boolean exactMatch) {",
            "    test.Test$$ViewInjector.inject(finder, target, source, exactMatch);",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"field 'thing'\");",
            "    if (view != null) {",
            "      InjectUtils.setMember(target, target.getClass(), \"thing\", view, exactMatch);",
            "    }",
            "  }",
            "  public static void reset(test.TestOne target, boolean exactMatch) {",
            "    test.Test$$ViewInjector.reset(target, exactMatch);",
            "    InjectUtils.setMember(target, target.getClass(), \"thing\", null, exactMatch);",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
.compilesWithoutError().and()
        .generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void genericSuperclass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "import butterknife.Optional;",
        "public class Test<T> extends Activity {",
        "  @InjectView(1) View view;",
        "}",
        "class TestOne extends Test<String> {",
        "  @InjectView(1) View thing;",
        "}",
        "class TestTwo extends Test<Object> {",
        "}"
    ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.InjectUtils;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source, boolean exactMatch) {",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"field 'view'\");",
            "    if (view != null) {",
            "      InjectUtils.setMember(target, target.getClass(), \"view\", view, exactMatch);",
            "    }",
            "  }",
            "  public static void reset(test.Test target, boolean exactMatch) {",
            "    InjectUtils.setMember(target, target.getClass(), \"view\", null, exactMatch);",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestOne$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.InjectUtils;",
            "public class TestOne$$ViewInjector {",
            "  public static void inject(Finder finder, final test.TestOne target, Object source, boolean exactMatch) {",
            "    test.Test$$ViewInjector.inject(finder, target, source, exactMatch);",
            "    View view;",
            "    view = finder.findRequiredView(source, 1, \"field 'thing'\");",
            "    if (view != null) {",
            "      InjectUtils.setMember(target, target.getClass(), \"thing\", view, exactMatch);",
            "    }",
            "  }",
            "  public static void reset(test.TestOne target, boolean exactMatch) {",
            "    test.Test$$ViewInjector.reset(target, exactMatch);",
            "    InjectUtils.setMember(target, target.getClass(), \"thing\", null, exactMatch);",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void failsInJavaPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package java.test;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "public class Test {",
        "  @InjectView(1) View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            "@InjectView-annotated class incorrectly in Java framework package. (java.test.Test)")
        .in(source).onLine(5);
  }

  @Test public void failsInAndroidPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package android.test;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "public class Test {",
        "  @InjectView(1) View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            "@InjectView-annotated class incorrectly in Android framework package. (android.test.Test)")
        .in(source).onLine(5);
  }

  @Test public void failsIfInPrivateClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "public class Test {",
        "  private static class Inner {",
        "    @InjectView(1) View thing;",
        "  }",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            "@InjectView fields may not be contained in private classes. (test.Test.Inner.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.InjectView;",
        "public class Test extends Activity {",
        "  @InjectView(1) String thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining("@InjectView fields must extend from View. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfInInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "public interface Test {",
        "    @InjectView(1) View thing = null;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            "@InjectView fields may only be contained in classes. (test.Test.thing)")
        .in(source).onLine(4);
  }

  @Test public void failsIfStatic() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "public class Test extends Activity {",
        "    @InjectView(1) static View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining("@InjectView fields must not be static. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfBothAnnotations() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "import butterknife.InjectViews;",
        "public class Test extends Activity {",
        "    @InjectView(1) @InjectViews(1) View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            "Only one of @InjectView and @InjectViews is allowed. (test.Test.thing)")
        .in(source).onLine(7);
  }

  @Test public void failsIfAlreadyInjected() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "public class Test extends Activity {",
        "    @InjectView(1) View thing1;",
        "    @InjectView(1) View thing2;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            "Attempt to use @InjectView for an already injected ID 1 on 'thing1'. (test.Test.thing2)")
        .in(source).onLine(7);
  }

  @Test public void failsRootViewInjectionWithBadTarget() throws Exception {
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
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining((
            "@OnItemClick annotation without an ID may only be used with an object of type "
                + "\"android.widget.AdapterView<?>\". (test.Test.doStuff)"))
        .in(source)
        .onLine(6);
  }

  @Test public void failsOptionalRootViewInjection() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.view.View;",
            "import butterknife.Optional;",
            "import butterknife.OnClick;",
            "public class Test extends View {",
            "  @Optional @OnClick void doStuff() {}",
            "  public Test(Context context) {",
            "    super(context);",
            "  }",
            "}"));

    ASSERT.about(javaSource())
        .that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            ("ID free injection must not be annotated with @Optional. (test.Test.doStuff)"))
        .in(source)
        .onLine(7);
  }
}
