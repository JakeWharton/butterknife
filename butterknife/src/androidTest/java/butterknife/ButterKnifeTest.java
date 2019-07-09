package butterknife;

import android.content.Context;
import android.view.View;
import androidx.test.InstrumentationRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class ButterKnifeTest {
  private final Context context = InstrumentationRegistry.getContext();

  @Before @After // Clear out cache of binders before and after each test.
  public void resetViewsCache() {
    ButterKnife.BINDINGS.clear();
  }

  @Test public void zeroBindingsBindDoesNotThrowExceptionAndCaches() {
    class Example {
    }

    Example example = new Example();
    View view = new View(context);
    assertThat(ButterKnife.BINDINGS).isEmpty();
    assertThat(ButterKnife.bind(example, view)).isSameAs(Unbinder.EMPTY);
    assertThat(ButterKnife.BINDINGS).containsEntry(Example.class, null);
  }

  @Test public void bindingKnownPackagesIsNoOp() {
    View view = new View(context);
    ButterKnife.bind(view);
    assertThat(ButterKnife.BINDINGS).isEmpty();
    ButterKnife.bind(new Object(), view);
    assertThat(ButterKnife.BINDINGS).isEmpty();
  }
}
