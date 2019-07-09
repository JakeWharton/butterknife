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
import butterknife.OnItemClick;
import butterknife.Optional;
import butterknife.Unbinder;
import com.example.butterknife.BuildConfig;
import com.example.butterknife.library.SimpleAdapter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assume.assumeFalse;

@SuppressWarnings("unused") // Used reflectively / by code gen.
public final class OnItemClickTest {
    static class TestSpinner extends AbsSpinner {
        public TestSpinner(Context context) {
            super(context);
            setAdapter(new SimpleAdapter(context));
        }

        void performItemClick(int position) {
            if (position < 0) {
                return;
            }

            AdapterView.OnItemClickListener listener = getOnItemClickListener();
            if (listener != null) {
                listener.onItemClick(this, null, position, NO_ID);
            }
        }
    }

    static final class Simple {
        int clickedPosition = -1;

        @OnItemClick(1) void itemClick(int position) {
            clickedPosition = position;
        }
    }

    @UiThreadTest
    @Test public void simple() {
        View tree = ViewTree.create(TestSpinner.class, 1);
        TestSpinner spinner = tree.findViewById(1);

        Simple target = new Simple();
        Unbinder unbinder = ButterKnife.bind(target, tree);
        assertEquals(-1, target.clickedPosition);

        spinner.performItemClick(0);
        assertEquals(0, target.clickedPosition);

        unbinder.unbind();
        spinner.performItemClick(1);
        assertEquals(0, target.clickedPosition);
    }


    static final class MultipleBindings {
        int clickedPosition1 = -1;
        int clickedPosition2 = -1;

        @OnItemClick(1) void itemClick1(int position) {
            clickedPosition1 = position;
        }

        @OnItemClick(1) void itemClick2(int position) {
            clickedPosition2 = position;
        }
    }

    @UiThreadTest
    @Test public void multipleBindings() {
        assumeFalse("Not implemented", BuildConfig.FLAVOR.equals("reflect")); // TODO

        View tree = ViewTree.create(TestSpinner.class, 1);
        TestSpinner spinner = tree.findViewById(1);

        MultipleBindings target = new MultipleBindings();
        Unbinder unbinder = ButterKnife.bind(target, tree);
        assertEquals(-1, target.clickedPosition1);
        assertEquals(-1, target.clickedPosition2);

        spinner.performItemClick(0);
        assertEquals(0, target.clickedPosition1);
        assertEquals(0, target.clickedPosition2);

        unbinder.unbind();
        spinner.performItemClick(1);
        assertEquals(0, target.clickedPosition1);
        assertEquals(0, target.clickedPosition2);
    }


    static final class Visibilities {
        int clickedPosition = -1;

        @OnItemClick(1) public void publicItemClick(int position) {
            clickedPosition = position;
        }

        @OnItemClick(2) void packageItemClick(int position) {
            clickedPosition = position;
        }

        @OnItemClick(3) protected void protectedItemClick(int position) {
            clickedPosition = position;
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

        spinner1.performItemClick(0);
        assertEquals(0, target.clickedPosition);

        spinner2.performItemClick(1);
        assertEquals(1, target.clickedPosition);

        spinner3.performItemClick(2);
        assertEquals(2, target.clickedPosition);
    }

    static final class MultipleIds {
        int clickedPosition = -1;

        @OnItemClick({1, 2}) void itemClick(int position) {
            clickedPosition = position;
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

        spinner1.performItemClick(0);
        assertEquals(0, target.clickedPosition);

        spinner2.performItemClick(1);
        assertEquals(1, target.clickedPosition);

        unbinder.unbind();
        spinner1.performItemClick(2);
        assertEquals(1, target.clickedPosition);
        spinner2.performItemClick(2);
        assertEquals(1, target.clickedPosition);
    }

    static final class OptionalId {
        int clickedPosition = -1;

        @Optional @OnItemClick(1) void itemClick(int position) {
            clickedPosition = position;
        }
    }

    @UiThreadTest
    @Test public void optionalIdPresent() {
        View tree = ViewTree.create(TestSpinner.class, 1);
        TestSpinner spinner = tree.findViewById(1);

        OptionalId target = new OptionalId();
        Unbinder unbinder = ButterKnife.bind(target, tree);
        assertEquals(-1, target.clickedPosition);

        spinner.performItemClick(0);
        assertEquals(0, target.clickedPosition);

        unbinder.unbind();
        spinner.performItemClick(1);
        assertEquals(0, target.clickedPosition);
    }

    @UiThreadTest
    @Test public void optionalIdAbsent() {
        View tree = ViewTree.create(TestSpinner.class, 2);
        TestSpinner spinner = tree.findViewById(2);

        OptionalId target = new OptionalId();
        Unbinder unbinder = ButterKnife.bind(target, tree);
        assertEquals(-1, target.clickedPosition);

        spinner.performItemClick(0);
        assertEquals(-1, target.clickedPosition);

        unbinder.unbind();
        spinner.performItemClick(0);
        assertEquals(-1, target.clickedPosition);
    }

    static final class ArgumentCast {
        interface MyInterface {}

        View last;

        @OnItemClick(1) void itemClickAdapterView(AdapterView<?> view) {
            last = view;
        }

        @OnItemClick(2) void itemClickAbsSpinner(AbsSpinner view) {
            last = view;
        }

        @OnItemClick(3) void itemClickMyInterface(ArgumentCast.MyInterface view) {
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

        spinner1.performItemClick(0);
        assertSame(spinner1, target.last);

        spinner2.performItemClick(0);
        assertSame(spinner2, target.last);

        spinner3.performItemClick(0);
        assertSame(spinner3, target.last);
    }
}
