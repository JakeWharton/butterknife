package com.example.butterknife.functional;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsSpinner;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import androidx.test.InstrumentationRegistry;
import androidx.test.annotation.UiThreadTest;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import butterknife.Optional;
import butterknife.Unbinder;
import com.example.butterknife.BuildConfig;
import com.example.butterknife.library.SimpleAdapter;
import org.junit.Before;
import org.junit.Test;

import static butterknife.OnItemSelected.Callback.NOTHING_SELECTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assume.assumeFalse;

@SuppressWarnings("unused") // Used by code gen.
public final class OnItemSelectedTest {
  static class TestSpinner extends AbsSpinner {
    public TestSpinner(Context context) {
      super(context);
      setAdapter(new SimpleAdapter(context));
    }

    void performSelection(int position) {
      if (position < 0) {
        return;
      }

      AdapterView.OnItemSelectedListener listener = getOnItemSelectedListener();
      if (listener != null) {
        listener.onItemSelected(this, null, position, NO_ID);
      }
    }

    void clearSelection() {
      AdapterView.OnItemSelectedListener listener = getOnItemSelectedListener();
      if (listener != null) {
        listener.onNothingSelected(this);
      }
    }
  }

  @Before public void ignoreIfReflect() {
    assumeFalse("Not implemented", BuildConfig.FLAVOR.equals("reflect")); // TODO
  }

  static final class Simple {
    int selectedPosition = -1;

    @OnItemSelected(1) void select(int position) {
      selectedPosition = position;
    }

    @OnItemSelected(value = 1, callback = NOTHING_SELECTED) void clear() {
      selectedPosition = -1;
    }
  }

  @UiThreadTest
  @Test public void simple() {
    View tree = ViewTree.create(TestSpinner.class, 1);
    TestSpinner spinner = tree.findViewById(1);

    Simple target = new Simple();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(-1, target.selectedPosition);

    spinner.performSelection(0);
    assertEquals(0, target.selectedPosition);

    spinner.clearSelection();
    assertEquals(-1, target.selectedPosition);

    spinner.performSelection(1);
    unbinder.unbind();
    spinner.performSelection(0);
    assertEquals(1, target.selectedPosition);
    spinner.clearSelection();
    assertEquals(1, target.selectedPosition);
  }

  static final class MultipleBindings {
    int selectedPosition1 = -1;
    int selectedPosition2 = -1;

    @OnItemSelected(1) void select1(int position) {
      selectedPosition1 = position;
    }

    @OnItemSelected(1) void select2(int position) {
      selectedPosition2 = position;
    }

    @OnItemSelected(value = 1, callback = NOTHING_SELECTED) void clear1() {
      selectedPosition1 = -1;
    }

    @OnItemSelected(value = 1, callback = NOTHING_SELECTED) void clear2() {
      selectedPosition2 = -1;
    }
  }

  @UiThreadTest
  @Test public void multipleBindings() {
    View tree = ViewTree.create(TestSpinner.class, 1);
    TestSpinner spinner = tree.findViewById(1);

    MultipleBindings target = new MultipleBindings();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(-1, target.selectedPosition1);
    assertEquals(-1, target.selectedPosition2);

    spinner.performSelection(0);
    assertEquals(0, target.selectedPosition1);
    assertEquals(0, target.selectedPosition2);

    spinner.clearSelection();
    assertEquals(-1, target.selectedPosition1);
    assertEquals(-1, target.selectedPosition2);

    spinner.performSelection(1);
    unbinder.unbind();
    spinner.performSelection(0);
    assertEquals(1, target.selectedPosition1);
    assertEquals(1, target.selectedPosition2);
    spinner.clearSelection();
    assertEquals(1, target.selectedPosition1);
    assertEquals(1, target.selectedPosition2);
  }

  static final class Visibilities {
    int selectedPosition = -1;

    @OnItemSelected(1) public void publicSelect(int position) {
      selectedPosition = position;
    }

    @OnItemSelected(2) void packageSelect(int position) {
      selectedPosition = position;
    }

    @OnItemSelected(3) protected void protectedSelect(int position) {
      selectedPosition = position;
    }

    @OnItemSelected(value = 1, callback = NOTHING_SELECTED) public void publicClear() {
      selectedPosition = -1;
    }

    @OnItemSelected(value = 2, callback = NOTHING_SELECTED) void packageClear() {
      selectedPosition = -1;
    }

    @OnItemSelected(value = 3, callback = NOTHING_SELECTED) protected void protectedClear() {
      selectedPosition = -1;
    }
  }

  @UiThreadTest
  @Test public void visibilities() {
    View tree = ViewTree.create(TestSpinner.class, 1, 2, 3);
    TestSpinner spinner1 = tree.findViewById(1);
    TestSpinner spinner2 = tree.findViewById(2);
    TestSpinner spinner3 = tree.findViewById(3);

    Visibilities target = new Visibilities();
    ButterKnife.bind(target, tree);
    assertEquals(-1, target.selectedPosition);

    spinner1.performSelection(0);
    assertEquals(0, target.selectedPosition);

    spinner1.clearSelection();
    assertEquals(-1, target.selectedPosition);

    spinner2.performSelection(0);
    assertEquals(0, target.selectedPosition);

    spinner2.clearSelection();
    assertEquals(-1, target.selectedPosition);

    spinner3.performSelection(0);
    assertEquals(0, target.selectedPosition);

    spinner3.clearSelection();
    assertEquals(-1, target.selectedPosition);
  }

  static final class MultipleIdPermutation {
    int selectedPosition = -1;

    @OnItemSelected({1, 2}) void select(int position) {
      selectedPosition = position;
    }

    @OnItemSelected(value = {1, 3}, callback = NOTHING_SELECTED) void clear() {
      selectedPosition = -1;
    }
  }

  @UiThreadTest
  @Test public void multipleIdPermutation() {
    View tree = ViewTree.create(TestSpinner.class, 1, 2, 3);
    TestSpinner spinner1 = tree.findViewById(1);
    TestSpinner spinner2 = tree.findViewById(2);
    TestSpinner spinner3 = tree.findViewById(3);

    MultipleIdPermutation target = new MultipleIdPermutation();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(-1, target.selectedPosition);

    spinner1.performSelection(0);
    assertEquals(0, target.selectedPosition);

    spinner1.clearSelection();
    assertEquals(-1, target.selectedPosition);

    spinner2.performSelection(0);
    assertEquals(0, target.selectedPosition);

    spinner2.clearSelection();
    assertEquals(0, target.selectedPosition);

    spinner3.performSelection(1);
    assertEquals(0, target.selectedPosition);

    spinner3.clearSelection();
    assertEquals(-1, target.selectedPosition);

    spinner1.performSelection(1);
    unbinder.unbind();
    spinner1.performSelection(0);
    assertEquals(1, target.selectedPosition);
    spinner2.performSelection(0);
    assertEquals(1, target.selectedPosition);
    spinner3.performSelection(0);
    assertEquals(1, target.selectedPosition);
    spinner1.clearSelection();
    assertEquals(1, target.selectedPosition);
    spinner2.clearSelection();
    assertEquals(1, target.selectedPosition);
    spinner3.clearSelection();
    assertEquals(1, target.selectedPosition);
  }

  static final class OptionalId {
    int selectedPosition = -1;

    @Optional @OnItemSelected(1) void select(int position) {
      selectedPosition = position;
    }

    @Optional @OnItemSelected(value = 1, callback = NOTHING_SELECTED) void clear() {
      selectedPosition = -1;
    }
  }

  @UiThreadTest
  @Test public void optionalIdPresent() {
    View tree = ViewTree.create(TestSpinner.class, 1);
    TestSpinner spinner = tree.findViewById(1);

    OptionalId target = new OptionalId();
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(-1, target.selectedPosition);

    spinner.performSelection(0);
    assertEquals(0, target.selectedPosition);

    spinner.clearSelection();
    assertEquals(-1, target.selectedPosition);

    spinner.performSelection(1);
    unbinder.unbind();
    spinner.performSelection(0);
    assertEquals(1, target.selectedPosition);
    spinner.clearSelection();
    assertEquals(1, target.selectedPosition);
  }

  @UiThreadTest
  @Test public void optionalIdAbsent() {
    View tree = ViewTree.create(TestSpinner.class, 2);
    TestSpinner spinner = tree.findViewById(2);

    OptionalId target = new OptionalId();
    target.selectedPosition = 1;
    Unbinder unbinder = ButterKnife.bind(target, tree);
    assertEquals(1, target.selectedPosition);

    spinner.performSelection(0);
    assertEquals(1, target.selectedPosition);

    spinner.clearSelection();
    assertEquals(1, target.selectedPosition);

    unbinder.unbind();
    spinner.performSelection(1);
    assertEquals(1, target.selectedPosition);
    spinner.clearSelection();
    assertEquals(1, target.selectedPosition);
  }

  static final class ArgumentCast {
    interface MyInterface {}

    View last;

    @OnItemSelected(1) void selectAdapterView(AdapterView<?> view) {
      last = view;
    }

    @OnItemSelected(2) void selectAbsSpinner(AbsSpinner view) {
      last = view;
    }

    @OnItemSelected(3) void selectMyInterface(MyInterface view) {
      last = (View) view;
    }

    @OnItemSelected(value = 1, callback = NOTHING_SELECTED)
    void clearAdapterView(AdapterView<?> view) {
      last = view;
    }

    @OnItemSelected(value = 2, callback = NOTHING_SELECTED)
    void clearAbsSpinner(AbsSpinner view) {
      last = view;
    }

    @OnItemSelected(value = 3, callback = NOTHING_SELECTED)
    void clearMyInterface(MyInterface view) {
      last = (View) view;
    }
  }

  @UiThreadTest
  @Test public void argumentCast() {
    class MySpinner extends TestSpinner implements ArgumentCast.MyInterface {
      MySpinner(Context context) {
        super(context);
      }
    }

    Context context = InstrumentationRegistry.getContext();
    TestSpinner spinner1 = new MySpinner(context);
    spinner1.setId(1);
    TestSpinner spinner2 = new MySpinner(context);
    spinner2.setId(2);
    TestSpinner spinner3 = new MySpinner(context);
    spinner3.setId(3);
    ViewGroup tree = new FrameLayout(context);
    tree.addView(spinner1);
    tree.addView(spinner2);
    tree.addView(spinner3);

    ArgumentCast target = new ArgumentCast();
    ButterKnife.bind(target, tree);

    spinner1.performSelection(0);
    assertSame(spinner1, target.last);

    spinner2.performSelection(0);
    assertSame(spinner2, target.last);

    spinner3.performSelection(0);
    assertSame(spinner3, target.last);

    spinner1.clearSelection();
    assertSame(spinner1, target.last);

    spinner2.clearSelection();
    assertSame(spinner2, target.last);

    spinner3.clearSelection();
    assertSame(spinner3, target.last);
  }
}
