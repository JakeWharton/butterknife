package com.example.butterknife.functional;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.test.InstrumentationRegistry;
import androidx.test.annotation.UiThreadTest;
import butterknife.ButterKnife;
import butterknife.OnLongClick;
import butterknife.Optional;
import butterknife.Unbinder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("unused") // Used reflectively / by code gen.
public final class OnLongClickTest {
  static final class Simple {
    boolean returnValue = true;
    int clicks = 0;

    @OnLongClick(1) boolean click() {
      clicks++;
      return returnValue;
    }
  }

  @UiThreadTest
  @Test public void simple() {
    View tree = ViewTree.create(1);
    View view1 = tree.findViewById(1);

    Simple target = new Simple();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(0, target.clicks);

    assertTrue(view1.performLongClick());
    assertEquals(1, target.clicks);

    target.returnValue = false;
    assertFalse(view1.performLongClick());
    assertEquals(2, target.clicks);

    unbinder.unbind();
    view1.performLongClick();
    assertEquals(2, target.clicks);
  }

  static final class ReturnVoid {
    int clicks = 0;

    @OnLongClick(1) void click() {
      clicks++;
    }
  }

  @UiThreadTest
  @Test public void returnVoid() {
    View tree = ViewTree.create(1);
    View view1 = tree.findViewById(1);

    ReturnVoid target = new ReturnVoid();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(0, target.clicks);

    assertTrue(view1.performLongClick());
    assertEquals(1, target.clicks);

    unbinder.unbind();
    view1.performLongClick();
    assertEquals(1, target.clicks);
  }

  static final class Visibilities {
    int clicks = 0;

    @OnLongClick(1) public boolean publicClick() {
      clicks++;
      return true;
    }

    @OnLongClick(2) boolean packageClick() {
      clicks++;
      return true;
    }

    @OnLongClick(3) protected boolean protectedClick() {
      clicks++;
      return true;
    }
  }

  @UiThreadTest
  @Test public void visibilities() {
    View tree = ViewTree.create(1, 2, 3);
    View view1 = tree.findViewById(1);
    View view2 = tree.findViewById(2);
    View view3 = tree.findViewById(3);

    Visibilities target = new Visibilities();
    ButterKnife.bind(target, tree);
    assertEquals(0, target.clicks);

    view1.performLongClick();
    assertEquals(1, target.clicks);

    view2.performLongClick();
    assertEquals(2, target.clicks);

    view3.performLongClick();
    assertEquals(3, target.clicks);
  }

  static final class MultipleIds {
    int clicks = 0;

    @OnLongClick({1, 2}) boolean click() {
      clicks++;
      return true;
    }
  }

  @UiThreadTest
  @Test public void multipleIds() {
    View tree = ViewTree.create(1, 2);
    View view1 = tree.findViewById(1);
    View view2 = tree.findViewById(2);

    MultipleIds target = new MultipleIds();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(0, target.clicks);

    view1.performLongClick();
    assertEquals(1, target.clicks);

    view2.performLongClick();
    assertEquals(2, target.clicks);

    unbinder.unbind();
    view1.performLongClick();
    view2.performLongClick();
    assertEquals(2, target.clicks);
  }

  static final class OptionalId {
    int clicks = 0;

    @Optional @OnLongClick(1) public boolean click() {
      clicks++;
      return true;
    }
  }

  @UiThreadTest
  @Test public void optionalIdPresent() {
    View tree = ViewTree.create(1);
    View view1 = tree.findViewById(1);

    OptionalId target = new OptionalId();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(0, target.clicks);

    view1.performLongClick();
    assertEquals(1, target.clicks);

    unbinder.unbind();
    view1.performLongClick();
    assertEquals(1, target.clicks);
  }

  @UiThreadTest
  @Test public void optionalIdAbsent() {
    View tree = ViewTree.create(2);
    View view2 = tree.findViewById(2);

    OptionalId target = new OptionalId();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(0, target.clicks);

    view2.performLongClick();
    assertEquals(0, target.clicks);

    unbinder.unbind();
    view2.performLongClick();
    assertEquals(0, target.clicks);
  }

  static final class ArgumentCast {
    interface MyInterface {}

    View last;

    @OnLongClick(1) boolean clickView(View view) {
      last = view;
      return true;
    }

    @OnLongClick(2) boolean clickTextView(TextView view) {
      last = view;
      return true;
    }

    @OnLongClick(3) boolean clickButton(Button view) {
      last = view;
      return true;
    }

    @OnLongClick(4) boolean clickMyInterface(MyInterface view) {
      last = (View) view;
      return true;
    }
  }

  @UiThreadTest
  @Test public void argumentCast() {
    class MyView extends Button implements ArgumentCast.MyInterface {
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
    View view4 = new MyView(InstrumentationRegistry.getContext());
    view4.setId(4);
    ViewGroup tree = new FrameLayout(InstrumentationRegistry.getContext());
    tree.addView(view1);
    tree.addView(view2);
    tree.addView(view3);
    tree.addView(view4);

    ArgumentCast target = new ArgumentCast();
    ButterKnife.bind(target, tree);

    view1.performLongClick();
    assertSame(view1, target.last);

    view2.performLongClick();
    assertSame(view2, target.last);

    view3.performLongClick();
    assertSame(view3, target.last);

    view4.performLongClick();
    assertSame(view4, target.last);
  }
}
