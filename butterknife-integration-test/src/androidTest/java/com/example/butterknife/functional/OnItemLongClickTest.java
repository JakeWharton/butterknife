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
import butterknife.OnItemLongClick;
import butterknife.Optional;
import butterknife.Unbinder;
import com.example.butterknife.library.SimpleAdapter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("unused") // Used reflectively / by code gen.
public final class OnItemLongClickTest {
    static class TestSpinner extends AbsSpinner {
        public TestSpinner(Context context) {
            super(context);
            setAdapter(new SimpleAdapter(context));
        }

        boolean performItemLongClick(int position) {
            if (position >= 0) {
                AdapterView.OnItemLongClickListener listener = getOnItemLongClickListener();
                if (listener != null) {
                    return listener.onItemLongClick(this, null, position, NO_ID);
                }
            }

            return false;
        }
    }

    static final class Simple {
        boolean returnValue = true;
        int clickedPosition = -1;

        @OnItemLongClick(1) boolean itemClick(int position) {
            clickedPosition = position;
            return returnValue;
        }
    }

    @UiThreadTest
    @Test public void simple() {
        View tree = ViewTree.create(TestSpinner.class, 1);
        TestSpinner spinner = tree.findViewById(1);

        Simple target = new Simple();
        Unbinder unbinder = ButterKnife.bind(target, tree);
        assertEquals(-1, target.clickedPosition);

        assertTrue(spinner.performItemLongClick(0));
        assertEquals(0, target.clickedPosition);

        target.returnValue = false;
        assertFalse(spinner.performItemLongClick(1));
        assertEquals(1, target.clickedPosition);

        unbinder.unbind();
        spinner.performItemLongClick(2);
        assertEquals(1, target.clickedPosition);
    }

    static final class ReturnVoid {
        int clickedPosition = -1;

        @OnItemLongClick(1) void itemLongClick(int position) {
            clickedPosition = position;
        }
    }

    @UiThreadTest
    @Test public void returnVoid() {
        View tree = ViewTree.create(TestSpinner.class, 1);
        TestSpinner spinner = tree.findViewById(1);

        ReturnVoid target = new ReturnVoid();
        Unbinder unbinder = ButterKnife.bind(target, tree);
        assertEquals(-1, target.clickedPosition);

        assertTrue(spinner.performItemLongClick(0));
        assertEquals(0, target.clickedPosition);

        unbinder.unbind();
        spinner.performItemLongClick(1);
        assertEquals(0, target.clickedPosition);
    }

    static final class Visibilities {
        int clickedPosition = -1;

        @OnItemLongClick(1) public boolean publicItemLongClick(int position) {
            clickedPosition = position;
            return true;
        }

        @OnItemLongClick(2) boolean packageItemLongClick(int position) {
            clickedPosition = position;
            return true;
        }

        @OnItemLongClick(3) protected boolean protectedItemLongClick(int position) {
            clickedPosition = position;
            return true;
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
        assertEquals(-1, target.clickedPosition);

        spinner1.performItemLongClick(0);
        assertEquals(0, target.clickedPosition);

        spinner2.performItemLongClick(1);
        assertEquals(1, target.clickedPosition);

        spinner3.performItemLongClick(2);
        assertEquals(2, target.clickedPosition);
    }

    static final class MultipleIds {
        int clickedPosition = -1;

        @OnItemLongClick({1, 2}) boolean itemLongClick(int position) {
            clickedPosition = position;
            return true;
        }
    }

    @UiThreadTest
    @Test public void multipleIds() {
        View tree = ViewTree.create(TestSpinner.class, 1, 2);
        TestSpinner spinner1 = tree.findViewById(1);
        TestSpinner spinner2 = tree.findViewById(2);

        MultipleIds target = new MultipleIds();
        Unbinder unbinder = ButterKnife.bind(target, tree);
        assertEquals(-1, target.clickedPosition);

        spinner1.performItemLongClick(0);
        assertEquals(0, target.clickedPosition);

        spinner2.performItemLongClick(1);
        assertEquals(1, target.clickedPosition);

        unbinder.unbind();
        spinner1.performItemLongClick(2);
        assertEquals(1, target.clickedPosition);
        spinner2.performItemLongClick(2);
        assertEquals(1, target.clickedPosition);
    }

    static final class OptionalId {
        int clickedPosition = -1;

        @Optional @OnItemLongClick(1) boolean itemLongClick(int position) {
            clickedPosition = position;
            return true;
        }
    }

    @UiThreadTest
    @Test public void optionalIdPresent() {
        View tree = ViewTree.create(TestSpinner.class, 1);
        TestSpinner spinner = tree.findViewById(1);

        OptionalId target = new OptionalId();
        Unbinder unbinder = ButterKnife.bind(target, tree);
        assertEquals(-1, target.clickedPosition);

        spinner.performItemLongClick(0);
        assertEquals(0, target.clickedPosition);

        unbinder.unbind();
        spinner.performItemLongClick(1);
        assertEquals(0, target.clickedPosition);
    }

    @UiThreadTest
    @Test public void optionalIdAbsent() {
        View tree = ViewTree.create(TestSpinner.class, 2);
        TestSpinner spinner = tree.findViewById(2);

        OptionalId target = new OptionalId();
        Unbinder unbinder = ButterKnife.bind(target, tree);
        assertEquals(-1, target.clickedPosition);

        spinner.performItemLongClick(0);
        assertEquals(-1, target.clickedPosition);

        unbinder.unbind();
        spinner.performItemLongClick(0);
        assertEquals(-1, target.clickedPosition);
    }

    static final class ArgumentCast {
        interface MyInterface {}

        View last;

        @OnItemLongClick(1) boolean itemLongClickAdapterView(AdapterView<?> view) {
            last = view;
            return true;
        }

        @OnItemLongClick(2) boolean itemLongClickAbsSpinner(AbsSpinner view) {
            last = view;
            return true;
        }

        @OnItemLongClick(3) boolean itemLongClickMyInterface(ArgumentCast.MyInterface view) {
            last = (View) view;
            return true;
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

        spinner1.performItemLongClick(0);
        assertSame(spinner1, target.last);

        spinner2.performItemLongClick(0);
        assertSame(spinner2, target.last);

        spinner3.performItemLongClick(0);
        assertSame(spinner3, target.last);
    }
}
