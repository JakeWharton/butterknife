package com.example.butterknife;

import com.example.butterknife.library.SimpleActivity;

@SuppressWarnings("deprecation") // ignore, simple test is simple, Espresso is too heavy for this
public final class SimpleActivityTest
        extends android.test.ActivityInstrumentationTestCase2<SimpleActivity> {
  public SimpleActivityTest() {
    super(SimpleActivity.class);
  }

  public void testActivityStarts() {
    getActivity(); // Trigger activity creation.
    getInstrumentation().waitForIdleSync(); // Wait for it to complete startup.
  }
}
