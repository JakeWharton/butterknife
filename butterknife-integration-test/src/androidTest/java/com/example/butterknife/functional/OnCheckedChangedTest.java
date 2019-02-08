package com.example.butterknife.functional;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ToggleButton;
import androidx.test.InstrumentationRegistry;
import androidx.test.annotation.UiThreadTest;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.Optional;
import butterknife.Unbinder;
import com.example.butterknife.BuildConfig;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assume.assumeFalse;

@SuppressWarnings("unused") // Used reflectively / by code gen.
public final class OnCheckedChangedTest {
  static final class Simple {
    int clicks = 0;

    @OnCheckedChanged(1) void click() {
      clicks++;
    }
  }

  @UiThreadTest
  @Test public void simple() {
    View tree = ViewTree.create(ToggleButton.class, 1);
    View view1 = tree.findViewById(1);

    Simple target = new Simple();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(0, target.clicks);

    view1.performClick();
    assertEquals(1, target.clicks);

    unbinder.unbind();
    view1.performClick();
    assertEquals(1, target.clicks);
  }

  static final class MultipleBindings {
    int clicks = 0;

    @OnCheckedChanged(1) void click1() {
      clicks++;
    }

    @OnCheckedChanged(1) void clicks2() {
      clicks++;
    }
  }

  @UiThreadTest
  @Test public void multipleBindings() {
    assumeFalse("Not implemented", BuildConfig.FLAVOR.equals("reflect")); // TODO

    View tree = ViewTree.create(ToggleButton.class, 1);
    View view1 = tree.findViewById(1);

    MultipleBindings target = new MultipleBindings();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(0, target.clicks);

    view1.performClick();
    assertEquals(2, target.clicks);

    unbinder.unbind();
    view1.performClick();
    assertEquals(2, target.clicks);
  }

  static final class Visibilities {
    int clicks = 0;

    @OnCheckedChanged(1) public void publicClick() {
      clicks++;
    }

    @OnCheckedChanged(2) void packageClick() {
      clicks++;
    }

    @OnCheckedChanged(3) protected void protectedClick() {
      clicks++;
    }
  }

  @UiThreadTest
  @Test public void visibilities() {
    View tree = ViewTree.create(ToggleButton.class, 1, 2, 3);
    View view1 = tree.findViewById(1);
    View view2 = tree.findViewById(2);
    View view3 = tree.findViewById(3);

    Visibilities target = new Visibilities();
    ButterKnife.bind(target, tree);
    assertEquals(0, target.clicks);

    view1.performClick();
    assertEquals(1, target.clicks);

    view2.performClick();
    assertEquals(2, target.clicks);

    view3.performClick();
    assertEquals(3, target.clicks);
  }

  static final class MultipleIds {
    int clicks = 0;

    @OnCheckedChanged({1, 2}) void click() {
      clicks++;
    }
  }

  @UiThreadTest
  @Test public void multipleIds() {
    View tree = ViewTree.create(ToggleButton.class, 1, 2);
    View view1 = tree.findViewById(1);
    View view2 = tree.findViewById(2);

    MultipleIds target = new MultipleIds();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(0, target.clicks);

    view1.performClick();
    assertEquals(1, target.clicks);

    view2.performClick();
    assertEquals(2, target.clicks);

    unbinder.unbind();
    view1.performClick();
    view2.performClick();
    assertEquals(2, target.clicks);
  }

  static final class OptionalId {
    int clicks = 0;

    @Optional @OnCheckedChanged(1) public void click() {
      clicks++;
    }
  }

  @UiThreadTest
  @Test public void optionalIdPresent() {
    View tree = ViewTree.create(ToggleButton.class, 1);
    View view1 = tree.findViewById(1);

    OptionalId target = new OptionalId();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(0, target.clicks);

    view1.performClick();
    assertEquals(1, target.clicks);

    unbinder.unbind();
    view1.performClick();
    assertEquals(1, target.clicks);
  }

  @UiThreadTest
  @Test public void optionalIdAbsent() {
    View tree = ViewTree.create(ToggleButton.class, 2);
    View view2 = tree.findViewById(2);

    OptionalId target = new OptionalId();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(0, target.clicks);

    view2.performClick();
    assertEquals(0, target.clicks);

    unbinder.unbind();
    view2.performClick();
    assertEquals(0, target.clicks);
  }

  static final class ArgumentCast {
    interface MyInterface {}

    View last;

    @OnCheckedChanged(1) void clickTextView(CompoundButton view) {
      last = view;
    }

    @OnCheckedChanged(2) void clickButton(ToggleButton view) {
      last = view;
    }

    @OnCheckedChanged(3) void clickMyInterface(MyInterface view) {
      last = (View) view;
    }
  }

  @UiThreadTest
  @Test public void argumentCast() {
    class MyView extends ToggleButton implements ArgumentCast.MyInterface {
      MyView(Context context) {
        super(context);
      }
    }

    View view1 = new MyView(InstrumentationRegistry.getContext());
    view1.setId(1);
    View view2 = new MyView(InstrumentationRegistry.getContext());
    view2.setId(2);
    View view3 = new MyView(InstrumentationRegistry.getContext());
    view3.setId(3);
    ViewGroup tree = new FrameLayout(InstrumentationRegistry.getContext());
    tree.addView(view1);
    tree.addView(view2);
    tree.addView(view3);

    ArgumentCast target = new ArgumentCast();
    ButterKnife.bind(target, tree);

    view1.performClick();
    assertSame(view1, target.last);

    view2.performClick();
    assertSame(view2, target.last);

    view3.performClick();
    assertSame(view3, target.last);
  }
}
