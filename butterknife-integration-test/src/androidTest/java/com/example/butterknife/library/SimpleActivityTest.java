package com.example.butterknife.library;

import androidx.test.rule.ActivityTestRule;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.butterknife.R;
import org.junit.Rule;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class SimpleActivityTest {
  @Rule public final ActivityTestRule<SimpleActivity> activityRule =
      new ActivityTestRule<>(SimpleActivity.class);

  @Test public void verifyContentViewBinding() {
    SimpleActivity activity = activityRule.getActivity();

    Unbinder unbinder = ButterKnife.bind(activity);
    verifySimpleActivityBound(activity);
    unbinder.unbind();
    verifySimpleActivityUnbound(activity);
  }

  protected static void verifySimpleActivityBound(SimpleActivity activity) {
    assertThat(activity.title.getId()).isEqualTo(R.id.titleTv);
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
