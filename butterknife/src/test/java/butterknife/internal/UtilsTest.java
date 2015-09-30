package butterknife.internal;

import org.junit.Test;

import static butterknife.internal.Utils.arrayOf;
import static butterknife.internal.Utils.listOf;
import static org.fest.assertions.api.Assertions.assertThat;

public final class UtilsTest {
  @Test public void listOfFiltersNull() {
    assertThat(listOf(null, null, null)).isEmpty();
    assertThat(listOf("One", null, null)).containsExactly("One");
    assertThat(listOf(null, "One", null)).containsExactly("One");
    assertThat(listOf(null, null, "One")).containsExactly("One");
    assertThat(listOf("One", "Two", null)).containsExactly("One", "Two");
    assertThat(listOf("One", null, "Two")).containsExactly("One", "Two");
    assertThat(listOf(null, "One", "Two")).containsExactly("One", "Two");
  }

  @Test public void arrayOfFiltersNull() {
    assertThat(arrayOf(null, null, null)).isEmpty();
    assertThat(arrayOf("One", null, null)).containsExactly("One");
    assertThat(arrayOf(null, "One", null)).containsExactly("One");
    assertThat(arrayOf(null, null, "One")).containsExactly("One");
    assertThat(arrayOf("One", "Two", null)).containsExactly("One", "Two");
    assertThat(arrayOf("One", null, "Two")).containsExactly("One", "Two");
    assertThat(arrayOf(null, "One", "Two")).containsExactly("One", "Two");
  }
}
