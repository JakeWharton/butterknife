package butterknife;

import java.lang.reflect.Method;
import org.junit.runners.model.InitializationError;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowApplication;

public class ButterKnifeTestRunner extends RobolectricTestRunner {
  public ButterKnifeTestRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }

  @Override public void setupApplicationState(Method testMethod) {
    Robolectric.application = ShadowApplication.bind(createApplication(), null, null);
  }
}
