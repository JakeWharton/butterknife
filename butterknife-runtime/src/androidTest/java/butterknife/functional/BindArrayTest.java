package butterknife.functional;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import butterknife.BindArray;
import butterknife.Unbinder;
import butterknife.runtime.test.R;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class BindArrayTest {
  private final Context context = InstrumentationRegistry.getContext();

  static class StringArrayTarget {
    @BindArray(R.array.string_one_two_three) String[] actual;
  }

  @Test public void asStringArray() {
    StringArrayTarget target = new StringArrayTarget();
    String[] expected = context.getResources().getStringArray(R.array.string_one_two_three);

    Unbinder unbinder = new BindArrayTest$StringArrayTarget_ViewBinding(target, context);
    assertThat(target.actual).isEqualTo(expected);

    unbinder.unbind();
    assertThat(target.actual).isEqualTo(expected);
  }

  static class IntArrayTarget {
    @BindArray(R.array.int_one_two_three) int[] actual;
  }

  @Test public void asIntArray() {
    IntArrayTarget target = new IntArrayTarget();
    int[] expected = context.getResources().getIntArray(R.array.int_one_two_three);

    Unbinder unbinder = new BindArrayTest$IntArrayTarget_ViewBinding(target, context);
    assertThat(target.actual).isEqualTo(expected);

    unbinder.unbind();
    assertThat(target.actual).isEqualTo(expected);
  }

  static class CharSequenceArrayTarget {
    @BindArray(R.array.int_one_two_three) CharSequence[] actual;
  }

  @Test public void asCharSequenceArray() {
    CharSequenceArrayTarget target = new CharSequenceArrayTarget();
    CharSequence[] expected = context.getResources().getTextArray(R.array.int_one_two_three);

    Unbinder unbinder = new BindArrayTest$CharSequenceArrayTarget_ViewBinding(target, context);
    assertThat(target.actual).isEqualTo(expected);

    unbinder.unbind();
    assertThat(target.actual).isEqualTo(expected);
  }
}
