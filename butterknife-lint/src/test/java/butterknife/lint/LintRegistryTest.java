package butterknife.lint;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class LintRegistryTest {

  @Test public void issues() throws Exception {
    assertThat(new LintRegistry().getIssues()).contains(InvalidR2UsageDetector.ISSUE);
  }
}
