package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class InjectViewsTest {
  @Test public void injectingArray() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectViews;",
        "public class Test extends Activity {",
        "    @InjectViews({1, 2, 3}) View[] thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.Injector;",
            "public class Test$$ViewInjector<T extends test.Test> implements Injector<T> {",
            "  @Override public void inject(Finder finder, final T target, Object source) {",
            "    View view;",
            "    target.thing = Finder.arrayOf(",
            "        finder.findRequiredView(source, 1, \"thing\"),",
            "        finder.findRequiredView(source, 2, \"thing\"),",
            "        finder.findRequiredView(source, 3, \"thing\")",
            "    );",
            "  }",
            "  @Override public void reset(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void injectingArrayWithGenerics() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectViews;",
        "public class Test<T extends View> extends Activity {",
        "    @InjectViews({1, 2, 3}) T[] thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.Injector;",
            "public class Test$$ViewInjector<T extends test.Test> implements Injector<T> {",
            "  @Override public void inject(Finder finder, final T target, Object source) {",
            "    View view;",
            "    target.thing = Finder.arrayOf(",
            "        finder.findRequiredView(source, 1, \"thing\"),",
            "        finder.findRequiredView(source, 2, \"thing\"),",
            "        finder.findRequiredView(source, 3, \"thing\")",
            "    );",
            "  }",
            "  @Override public void reset(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void injectingArrayWithCast() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.widget.TextView;",
        "import butterknife.InjectViews;",
        "public class Test extends Activity {",
        "    @InjectViews({1, 2, 3}) TextView[] thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.Injector;",
            "public class Test$$ViewInjector<T extends test.Test> implements Injector<T> {",
            "  @Override public void inject(Finder finder, final T target, Object source) {",
            "    View view;",
            "    target.thing = Finder.arrayOf(",
            "        (android.widget.TextView) finder.findRequiredView(source, 1, \"thing\"),",
            "        (android.widget.TextView) finder.findRequiredView(source, 2, \"thing\"),",
            "        (android.widget.TextView) finder.findRequiredView(source, 3, \"thing\")",
            "    );",
            "  }",
            "  @Override public void reset(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void injectingList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectViews;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "    @InjectViews({1, 2, 3}) List<View> thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.Injector;",
            "public class Test$$ViewInjector<T extends test.Test> implements Injector<T> {",
            "  @Override public void inject(Finder finder, final T target, Object source) {",
            "    View view;",
            "    target.thing = Finder.listOf(",
            "        finder.findRequiredView(source, 1, \"thing\"),",
            "        finder.findRequiredView(source, 2, \"thing\"),",
            "        finder.findRequiredView(source, 3, \"thing\")",
            "    );",
            "  }",
            "  @Override public void reset(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void injectingListWithGenerics() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectViews;",
        "import java.util.List;",
        "public class Test<T extends View> extends Activity {",
        "    @InjectViews({1, 2, 3}) List<T> thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.Injector;",
            "public class Test$$ViewInjector<T extends test.Test> implements Injector<T> {",
            "  @Override public void inject(Finder finder, final T target, Object source) {",
            "    View view;",
            "    target.thing = Finder.listOf(",
            "        finder.findRequiredView(source, 1, \"thing\"),",
            "        finder.findRequiredView(source, 2, \"thing\"),",
            "        finder.findRequiredView(source, 3, \"thing\")",
            "    );",
            "  }",
            "  @Override public void reset(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void optional() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectViews;",
        "import butterknife.Optional;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "    @Optional @InjectViews({1, 2, 3}) List<View> thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.Injector;",
            "public class Test$$ViewInjector<T extends test.Test> implements Injector<T> {",
            "  @Override public void inject(Finder finder, final T target, Object source) {",
            "    View view;",
            "    target.thing = Finder.listOf(",
            "        finder.findOptionalView(source, 1),",
            "        finder.findOptionalView(source, 2),",
            "        finder.findOptionalView(source, 3)",
            "    );",
            "  }",
            "  @Override public void reset(T target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void failsIfNoIds() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.InjectViews;",
        "import java.util.List;",
        "public class Test {",
        "  @InjectViews({}) List<View> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining("@InjectViews must specify at least one ID. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfNoGenericType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import butterknife.InjectViews;",
        "import java.util.List;",
        "public class Test {",
        "  @InjectViews(1) List thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            "@InjectViews List must have a generic component. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfUnsupportedCollection() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.InjectViews;",
        "import java.util.Deque;",
        "public class Test {",
        "  @InjectViews(1) Deque<View> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            "@InjectViews must be a List or array. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfGenericNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.InjectViews;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "  @InjectViews(1) List<String> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining("@InjectViews type must extend from View. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfArrayNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;", "import android.app.Activity;", "import butterknife.InjectViews;",
        "public class Test extends Activity {", "  @InjectViews(1) String[] thing;", "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining("@InjectViews type must extend from View. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsInJavaPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package java.test;",
        "import android.view.View;",
        "import butterknife.InjectViews;",
        "public class Test {",
        "  @InjectViews(1) View[] thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            "@InjectViews-annotated class incorrectly in Java framework package. (java.test.Test)")
        .in(source).onLine(5);
  }

  @Test public void failsInAndroidPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package android.test;",
        "import android.view.View;",
        "import butterknife.InjectViews;",
        "public class Test {",
        "  @InjectViews(1) View[] thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            "@InjectViews-annotated class incorrectly in Android framework package. (android.test.Test)")
        .in(source).onLine(5);
  }

  @Test public void failsIfInPrivateClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.InjectViews;",
        "import java.util.List;",
        "public class Test {",
        "  private static class Inner {",
        "    @InjectViews(1) List<View> thing;",
        "  }",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            "@InjectViews fields may not be contained in private classes. (test.Test.Inner.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfInInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.InjectViews;",
        "import java.util.List;",
        "public interface Test {",
        "    @InjectViews(1) List<View> thing = null;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            "@InjectViews fields may only be contained in classes. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfPrivate() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectViews;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "    @InjectViews(1) private List<View> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining("@InjectViews fields must not be private or static. (test.Test.thing)")
        .in(source).onLine(7);
  }

  @Test public void failsIfStatic() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectViews;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "    @InjectViews(1) static List<View> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining("@InjectViews fields must not be private or static. (test.Test.thing)")
        .in(source).onLine(7);
  }

  @Test public void failsIfContainsDuplicateIds() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectViews;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "    @InjectViews({1, 1}) List<View> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            "@InjectViews annotation contains duplicate ID 1. (test.Test.thing)")
        .in(source).onLine(7);
  }
}
