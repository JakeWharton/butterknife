package com.example.butterknife;

import android.test.ActivityInstrumentationTestCase2;

public final class SimpleActivityTest extends ActivityInstrumentationTestCase2<SimpleActivity> {
  public SimpleActivityTest() {
    super(SimpleActivity.class);
  }

  public void testActivityStarts() {
    getActivity(); // Trigger activity creation.
    getInstrumentation().waitForIdleSync(); // Wait for it to complete startup.
  }
}
