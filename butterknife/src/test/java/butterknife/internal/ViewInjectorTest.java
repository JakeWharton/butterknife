package butterknife.internal;

import org.junit.Test;

import static butterknife.internal.ViewInjector.emitHumanDescription;
import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

public class ViewInjectorTest {
  @Test public void humanDescriptionJoinWorks() {
    Binding one = new TestBinding("one");
    Binding two = new TestBinding("two");
    Binding three = new TestBinding("three");

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

  private static class TestBinding implements Binding {
    private final String description;

    private TestBinding(String description) {
      this.description = description;
    }

    @Override public String getDescription() {
      return description;
    }
  }
}
