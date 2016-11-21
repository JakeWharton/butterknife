package butterknife.compiler;

import org.junit.Test;

import static butterknife.compiler.BindingSet.asHumanDescription;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class BindingSetTest {
  @Test public void humanDescriptionJoinWorks() {
    MemberViewBinding one = new TestViewBinding("one");
    MemberViewBinding two = new TestViewBinding("two");
    MemberViewBinding three = new TestViewBinding("three");

    String result1 = asHumanDescription(singletonList(one));
    assertThat(result1).isEqualTo("one");

    String result2 = asHumanDescription(asList(one, two));
    assertThat(result2).isEqualTo("one and two");

    String result3 = asHumanDescription(asList(one, two, three));
    assertThat(result3).isEqualTo("one, two, and three");
  }

  private static class TestViewBinding implements MemberViewBinding {
    private final String description;

    private TestViewBinding(String description) {
      this.description = description;
    }

    @Override public String getDescription() {
      return description;
    }
  }
}
