package butterknife.internal;

import org.junit.Test;

import static butterknife.internal.BindingClass.asHumanDescription;
import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

public class ViewBindingClassTest {
  @Test public void humanDescriptionJoinWorks() {
    ViewBinding one = new TestViewBinding("one");
    ViewBinding two = new TestViewBinding("two");
    ViewBinding three = new TestViewBinding("three");

    String result1 = asHumanDescription(asList(one));
    assertThat(result1).isEqualTo("one");

    String result2 = asHumanDescription(asList(one, two));
    assertThat(result2).isEqualTo("one and two");

    String result3 = asHumanDescription(asList(one, two, three));
    assertThat(result3).isEqualTo("one, two, and three");
  }

  private static class TestViewBinding implements ViewBinding {
    private final String description;

    private TestViewBinding(String description) {
      this.description = description;
    }

    @Override public String getDescription() {
      return description;
    }
  }
}
