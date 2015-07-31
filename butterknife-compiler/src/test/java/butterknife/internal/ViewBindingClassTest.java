package butterknife.internal;

import org.junit.Test;

import static butterknife.internal.BindingClass.emitHumanDescription;
import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

public class ViewBindingClassTest {
  @Test public void humanDescriptionJoinWorks() {
    ViewBinding one = new TestViewBinding("one");
    ViewBinding two = new TestViewBinding("two");
    ViewBinding three = new TestViewBinding("three");

    StringBuilder builder1 = new StringBuilder();
    emitHumanDescription(builder1, asList(one));
    assertThat(builder1.toString()).isEqualTo("one");

    StringBuilder builder2 = new StringBuilder();
    emitHumanDescription(builder2, asList(one, two));
    assertThat(builder2.toString()).isEqualTo("one and two");

    StringBuilder builder3 = new StringBuilder();
    emitHumanDescription(builder3, asList(one, two, three));
    assertThat(builder3.toString()).isEqualTo("one, two, and three");
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
