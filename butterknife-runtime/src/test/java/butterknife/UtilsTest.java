package butterknife;

import butterknife.internal.Utils;
import org.junit.Test;

import static butterknife.internal.Utils.arrayFilteringNull;
import static butterknife.internal.Utils.listFilteringNull;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class UtilsTest {
  @Test public void listOfFiltersNull() {
    assertThat(listFilteringNull(null, null, null)).isEmpty();
    assertThat(listFilteringNull("One", null, null)).containsExactly("One");
    assertThat(listFilteringNull(null, "One", null)).containsExactly("One");
    assertThat(listFilteringNull(null, null, "One")).containsExactly("One");
    assertThat(listFilteringNull("One", "Two", null)).containsExactly("One", "Two");
    assertThat(listFilteringNull("One", null, "Two")).containsExactly("One", "Two");
    assertThat(listFilteringNull(null, "One", "Two")).containsExactly("One", "Two");
    assertThat(listFilteringNull("One", "Two", "Three")).containsExactly("One", "Two", "Three");
  }

  @Test public void arrayFilteringNullRemovesNulls() {
    assertThat(arrayFilteringNull(null, null, null)).isEmpty();
    assertThat(arrayFilteringNull("One", null, null)).asList().containsExactly("One");
    assertThat(arrayFilteringNull(null, "One", null)).asList().containsExactly("One");
    assertThat(arrayFilteringNull(null, null, "One")).asList().containsExactly("One");
    assertThat(arrayFilteringNull("One", "Two", null)).asList().containsExactly("One", "Two");
    assertThat(arrayFilteringNull("One", null, "Two")).asList().containsExactly("One", "Two");
    assertThat(arrayFilteringNull(null, "One", "Two")).asList().containsExactly("One", "Two");
  }

  @Test public void arrayFilteringNullReturnsOriginalWhenNoNulls() {
    String[] input = { "One", "Two", "Three" };
    String[] actual = arrayFilteringNull(input);
    assertThat(actual).isSameAs(input);
    // Even though we got the same reference back check to ensure its contents weren't mutated.
    assertThat(actual).asList().containsExactly("One", "Two", "Three");
  }

  @Test public void testCastParam() {
    try {
      Utils.castParam("abc", "Foo", 3, "foo()", 4, Integer.class);
      fail();
    } catch (IllegalStateException ise) {
      assertThat(ise.getMessage()).isEqualTo(
          "Parameter #4 of method 'Foo' was of the wrong type for parameter #5 of method 'foo()'. See cause for more info.");
    }
  }
}
