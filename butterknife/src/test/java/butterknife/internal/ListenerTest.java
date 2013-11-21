package butterknife.internal;

import android.widget.AdapterView;
import java.math.BigInteger;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

// NOTE: This class uses single-method classes rather than interfaces as a trick to test easily.
public class ListenerTest {
  @Test public void typesWithoutOneMethodAreInvalid() {
    try {
      class ZeroMethods {
      }

      Listener.from(ZeroMethods.class);
      fail("One method is required.");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("ZeroMethods is not a single-method interface");
    }

    try {
      @SuppressWarnings("UnusedDeclaration") //
      class TwoMethods {
        public void methodOne() {}
        public void methodTwo() {}
      }

      Listener.from(TwoMethods.class);
      fail("One method is required.");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("TwoMethods is not a single-method interface");
    }
  }

  @SuppressWarnings("UnusedDeclaration") //
  interface SimpleCase {
    void foo(String foo, int bar, BigInteger baz);
  }

  @Test public void simpleCase() {
    Listener listener = Listener.from(SimpleCase.class);

    assertThat(listener.getOwnerType()).isEqualTo("butterknife.internal.ListenerTest");
    assertThat(listener.getSetterName()).isEqualTo("setSimpleCase");
    assertThat(listener.getType()).isEqualTo("butterknife.internal.ListenerTest.SimpleCase");
    assertThat(listener.getMethodName()).isEqualTo("foo");
    assertThat(listener.getReturnType()).isEqualTo("void");
    assertThat(listener.getParameterTypes()) //
        .containsExactly("String", "int", "java.math.BigInteger");
  }


  @SuppressWarnings("UnusedDeclaration") //
  interface GenericParameter {
    boolean foo(AdapterView<?> adapterView);
  }

  @Test public void genericParameter() {
    Listener listener = Listener.from(GenericParameter.class);

    assertThat(listener.getOwnerType()).isEqualTo("butterknife.internal.ListenerTest");
    assertThat(listener.getSetterName()).isEqualTo("setGenericParameter");
    assertThat(listener.getType()).isEqualTo("butterknife.internal.ListenerTest.GenericParameter");
    assertThat(listener.getMethodName()).isEqualTo("foo");
    assertThat(listener.getReturnType()).isEqualTo("boolean");
    assertThat(listener.getParameterTypes()).containsExactly("android.widget.AdapterView<?>");
  }
}
