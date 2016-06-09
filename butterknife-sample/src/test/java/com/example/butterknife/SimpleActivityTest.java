package com.example.butterknife;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
public class SimpleActivityTest {
  @Test public void verifyContentViewBinding() {
    SimpleActivity activity = Robolectric.buildActivity(SimpleActivity.class).create().get();

    Unbinder unbinder = ButterKnife.bind(activity);
    verifySimpleActivityBound(activity);
    unbinder.unbind();
    verifySimpleActivityUnbound(activity);
  }

  protected static void verifySimpleActivityBound(SimpleActivity activity) {
    assertThat(activity.title.getId()).isEqualTo(R.id.title);
    assertThat(activity.subtitle.getId()).isEqualTo(R.id.subtitle);
    assertThat(activity.hello.getId()).isEqualTo(R.id.hello);
    assertThat(activity.listOfThings.getId()).isEqualTo(R.id.list_of_things);
    assertThat(activity.footer.getId()).isEqualTo(R.id.footer);
  }

  protected static void verifySimpleActivityUnbound(SimpleActivity activity) {
    assertThat(activity.title).isNull();
    assertThat(activity.subtitle).isNull();
    assertThat(activity.hello).isNull();
    assertThat(activity.listOfThings).isNull();
    assertThat(activity.footer).isNull();
  }
}
