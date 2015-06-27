package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindViewsTest {
  @Test public void bindingArray() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "public class Test extends Activity {",
        "    @BindViews({1, 2, 3}) View[] thing;",
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
        "import butterknife.BindViews;",
        "public class Test<T extends View> extends Activity {",
        "    @BindViews({1, 2, 3}) T[] thing;",
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
        "import butterknife.BindViews;",
        "public class Test extends Activity {",
        "    @BindViews({1, 2, 3}) TextView[] thing;",
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
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "    @BindViews({1, 2, 3}) List<View> thing;",
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
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test {",
        "    interface TestInterface {}",
        "    @BindViews({1, 2, 3}) List<TestInterface> thing;",
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
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test<T extends View> extends Activity {",
        "    @BindViews({1, 2, 3}) List<T> thing;",
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

  @Test public void nullable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "    @interface Nullable {}",
        "    @Nullable @BindViews({1, 2, 3}) List<View> thing;",
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
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test {",
        "  @BindViews({}) List<View> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindViews must specify at least one ID. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfNoGenericType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test {",
        "  @BindViews(1) List thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindViews List must have a generic component. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfUnsupportedCollection() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "import java.util.Deque;",
        "public class Test {",
        "  @BindViews(1) Deque<View> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindViews must be a List or array. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfGenericNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "  @BindViews(1) List<String> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindViews type must extend from View or be an interface. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfArrayNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;", "import android.app.Activity;", "import butterknife.BindViews;",
        "public class Test extends Activity {", "  @BindViews(1) String[] thing;", "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindViews type must extend from View or be an interface. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsInJavaPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package java.test;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "public class Test {",
        "  @BindViews(1) View[] thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindViews-annotated class incorrectly in Java framework package. (java.test.Test)")
        .in(source).onLine(5);
  }

  @Test public void failsInAndroidPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package android.test;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "public class Test {",
        "  @BindViews(1) View[] thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindViews-annotated class incorrectly in Android framework package. (android.test.Test)")
        .in(source).onLine(5);
  }

  @Test public void failsIfInPrivateClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test {",
        "  private static class Inner {",
        "    @BindViews(1) List<View> thing;",
        "  }",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindViews fields may not be contained in private classes. (test.Test.Inner.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfInInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public interface Test {",
        "    @BindViews(1) List<View> thing = null;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindViews fields may only be contained in classes. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfPrivate() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "    @BindViews(1) private List<View> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindViews fields must not be private or static. (test.Test.thing)")
        .in(source).onLine(7);
  }

  @Test public void failsIfStatic() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "    @BindViews(1) static List<View> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindViews fields must not be private or static. (test.Test.thing)")
        .in(source).onLine(7);
  }

  @Test public void failsIfContainsDuplicateIds() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "    @BindViews({1, 1}) List<View> thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindViews annotation contains duplicate ID 1. (test.Test.thing)")
        .in(source).onLine(7);
  }
}
