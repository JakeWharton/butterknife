package butterknife.internal;

import org.junit.Test;

import static butterknife.internal.TargetClass.humanDescriptionJoin;
import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

public class TargetClassTest {
  @Test public void humanDescriptionJoinWorks() {
    Binding one = new TestBinding("one");
    Binding two = new TestBinding("two");
    Binding three = new TestBinding("three");

    assertThat(humanDescriptionJoin(asList(one))).isEqualTo("one");
    assertThat(humanDescriptionJoin(asList(one, two))).isEqualTo("one and two");
    assertThat(humanDescriptionJoin(asList(one, two, three))).isEqualTo("one, two, and three");
  }

  private static class TestBinding implements Binding {
    private final String description;

    private TestBinding(String description) {
      this.description = description;
    }

    @Override public String getDescription() {
      return description;
    }

    @Override public boolean isRequired() {
      throw new AssertionError();
    }
  }
}
