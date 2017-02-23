package butterknife.lint;

import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Issue;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class InvalidR2UsageDetectorTest extends LintDetectorTestBase {
  private static final String PATH_TEST_RESOURCES = "/src/test/java/sample/r2/";
  private static final String NO_WARNINGS = "No warnings.";
  private static final String R2 = "R2.java";
  private static final String BIND_TEST = "BindTest.java";

  @Override protected Detector getDetector() {
    return new InvalidR2UsageDetector();
  }

  @Override protected List<Issue> getIssues() {
    return ImmutableList.of(InvalidR2UsageDetector.ISSUE);
  }

  @Override protected String getTestResourcesPath() {
    return PATH_TEST_RESOURCES;
  }

  public void testNoR2Usage() throws Exception {
    String file = "NoR2Usage.java";
    assertSame(NO_WARNINGS, lintFiles(file));
  }

  public void testR2UsageInAnnotations() throws Exception {
    String file = "R2UsageInAnnotations.java";
    assertSame(NO_WARNINGS, lintFiles(file, BIND_TEST, R2));
  }

  public void testR2UsageOutsideAnnotations() throws Exception {
    String file = "R2UsageOutsideAnnotations.java";
    String lintOutput = lintFiles(file, R2);
    assertNotSame(NO_WARNINGS, lintOutput);
    assertTrue(lintOutput.contains("2 errors, 0 warnings"));
  }

  public void testR2UsageWithSuppression() throws Exception {
    String file = "R2UsageWithSuppression.java";
    String lintOutput = lintFiles(file, R2);
    assertSame(NO_WARNINGS, lintOutput);
  }
}
