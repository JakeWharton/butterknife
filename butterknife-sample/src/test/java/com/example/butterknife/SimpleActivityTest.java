package com.example.butterknife;

import butterknife.Views;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.ANDROID.assertThat;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class SimpleActivityTest {
  @Test public void verifyContentViewInjection() {
    SimpleActivity activity = new SimpleActivity();
    shadowOf(activity).callOnCreate(null);

    assertThat(activity.title).hasId(R.id.title);
    assertThat(activity.subtitle).hasId(R.id.subtitle);
    assertThat(activity.hello).hasId(R.id.hello);
    assertThat(activity.listOfThings).hasId(R.id.list_of_things);
    assertThat(activity.footer).hasId(R.id.footer);
  }

  @Test public void verifyContentViewEjection() {
    SimpleActivity activity = new SimpleActivity();
    shadowOf(activity).callOnCreate(null);
    Views.eject(activity);

    assertThat(activity.title).isNull();
    assertThat(activity.subtitle).isNull();
    assertThat(activity.hello).isNull();
    assertThat(activity.listOfThings).isNull();
    assertThat(activity.footer).isNull();
  }
}