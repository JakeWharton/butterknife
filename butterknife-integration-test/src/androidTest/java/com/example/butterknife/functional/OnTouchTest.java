package com.example.butterknife.functional;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.test.InstrumentationRegistry;
import androidx.test.annotation.UiThreadTest;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import butterknife.Optional;
import butterknife.Unbinder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("unused") // Used reflectively / by code gen.
public final class OnTouchTest {
  static final class Simple {
    boolean returnValue = true;
    int touches = 0;

    @OnTouch(1) boolean touch() {
      touches++;
      return returnValue;
    }
  }

  @UiThreadTest
  @Test public void simple() {
    View tree = ViewTree.create(1);
    View view1 = tree.findViewById(1);

    Simple target = new Simple();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(0, target.touches);

    assertTrue(performTouch(view1));
    assertEquals(1, target.touches);

    target.returnValue = false;
    assertFalse(performTouch(view1));
    assertEquals(2, target.touches);

    unbinder.unbind();
    performTouch(view1);
    assertEquals(2, target.touches);
  }

  static final class Arguments {
    int touches = 0;

    @OnTouch(1) boolean touch(View v) {
      touches++;
      return true;
    }

    @OnTouch(2) boolean touch(View v, MotionEvent event) {
      touches++;
      return true;
    }
  }

  @UiThreadTest
  @Test public void arguments() {
    View tree = ViewTree.create(1, 2);
    View view1 = tree.findViewById(1);
    View view2 = tree.findViewById(2);

    Arguments target = new Arguments();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(0, target.touches);

    assertTrue(performTouch(view1));
    assertEquals(1, target.touches);

    assertTrue(performTouch(view2));
    assertEquals(2, target.touches);

    unbinder.unbind();
    performTouch(view1);
    assertEquals(2, target.touches);
  }

  static final class ReturnVoid {
    int touches = 0;

    @OnTouch(1) void touch() {
      touches++;
    }
  }

  @UiThreadTest
  @Test public void returnVoid() {
    View tree = ViewTree.create(1);
    View view1 = tree.findViewById(1);

    ReturnVoid target = new ReturnVoid();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(0, target.touches);

    assertTrue(performTouch(view1));
    assertEquals(1, target.touches);

    unbinder.unbind();
    performTouch(view1);
    assertEquals(1, target.touches);
  }

  static final class Visibilities {
    int touches = 0;

    @OnTouch(1) public boolean publicTouch() {
      touches++;
      return true;
    }

    @OnTouch(2) boolean packageTouch() {
      touches++;
      return true;
    }

    @OnTouch(3) protected boolean protectedTouch() {
      touches++;
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
    assertEquals(0, target.touches);

    performTouch(view1);
    assertEquals(1, target.touches);

    performTouch(view2);
    assertEquals(2, target.touches);

    performTouch(view3);
    assertEquals(3, target.touches);
  }

  static final class MultipleIds {
    int touches = 0;

    @OnTouch({1, 2}) boolean touch() {
      touches++;
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
    assertEquals(0, target.touches);

    performTouch(view1);
    assertEquals(1, target.touches);

    performTouch(view2);
    assertEquals(2, target.touches);

    unbinder.unbind();
    performTouch(view1);
    performTouch(view2);
    assertEquals(2, target.touches);
  }

  static final class OptionalId {
    int touches = 0;

    @Optional @OnTouch(1) public boolean touch() {
      touches++;
      return true;
    }
  }

  @UiThreadTest
  @Test public void optionalIdPresent() {
    View tree = ViewTree.create(1);
    View view1 = tree.findViewById(1);

    OptionalId target = new OptionalId();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(0, target.touches);

    performTouch(view1);
    assertEquals(1, target.touches);

    unbinder.unbind();
    performTouch(view1);
    assertEquals(1, target.touches);
  }

  @UiThreadTest
  @Test public void optionalIdAbsent() {
    View tree = ViewTree.create(2);
    View view2 = tree.findViewById(2);

    OptionalId target = new OptionalId();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(0, target.touches);

    performTouch(view2);
    assertEquals(0, target.touches);

    unbinder.unbind();
    performTouch(view2);
    assertEquals(0, target.touches);
  }

  static final class ArgumentCast {
    interface MyInterface {}

    View last;

    @OnTouch(1) boolean touchView(View view) {
      last = view;
      return true;
    }

    @OnTouch(2) boolean touchTextView(TextView view) {
      last = view;
      return true;
    }

    @OnTouch(3) boolean touchButton(Button view) {
      last = view;
      return true;
    }

    @OnTouch(4) boolean touchMyInterface(ArgumentCast.MyInterface view) {
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

    Context context = InstrumentationRegistry.getContext();
    View view1 = new MyView(context);
    view1.setId(1);
    View view2 = new MyView(context);
    view2.setId(2);
    View view3 = new MyView(context);
    view3.setId(3);
    View view4 = new MyView(context);
    view4.setId(4);
    ViewGroup tree = new FrameLayout(context);
    tree.addView(view1);
    tree.addView(view2);
    tree.addView(view3);
    tree.addView(view4);

    ArgumentCast target = new ArgumentCast();
    ButterKnife.bind(target, tree);

    performTouch(view1);
    assertSame(view1, target.last);

    performTouch(view2);
    assertSame(view2, target.last);

    performTouch(view3);
    assertSame(view3, target.last);

    performTouch(view4);
    assertSame(view4, target.last);
  }

  private static boolean performTouch(View view) {
    MotionEvent event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0, 0, 0);
    return view.dispatchTouchEvent(event);
  }
}
