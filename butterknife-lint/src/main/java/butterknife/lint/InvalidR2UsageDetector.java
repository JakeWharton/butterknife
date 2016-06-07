package butterknife.lint;

import com.android.annotations.NonNull;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Context;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.util.Set;
import lombok.ast.Annotation;
import lombok.ast.AstVisitor;
import lombok.ast.ClassDeclaration;
import lombok.ast.ForwardingAstVisitor;
import lombok.ast.Identifier;
import lombok.ast.Node;
import lombok.ast.Select;
import lombok.ast.VariableReference;

/**
 * Custom lint rule to make sure that generated R2 is not referenced outside annotations.
 */
public class InvalidR2UsageDetector extends Detector implements Detector.JavaScanner {

  private static final String LINT_ERROR_BODY = "R2 should only be used inside annotations";
  private static final String LINT_ERROR_TITLE = "Invalid usage of R2";
  private static final String ISSUE_ID = "InvalidR2Usage";
  private static final Set<String> SUPPORTED_TYPES =
      ImmutableSet.of("array", "attr", "bool", "color", "dimen", "drawable", "id", "integer",
          "string");

  static final Issue ISSUE =
      Issue.create(ISSUE_ID, LINT_ERROR_TITLE, LINT_ERROR_BODY, Category.CORRECTNESS, 6,
          Severity.ERROR, new Implementation(InvalidR2UsageDetector.class, Scope.JAVA_FILE_SCOPE));

  private static final String R2 = "R2";

  @Override public boolean appliesTo(@NonNull Context context, @NonNull File file) {
    // skip generated files
    return !file.getName().contains("$$");
  }

  @Override public AstVisitor createJavaVisitor(@NonNull JavaContext javaContext) {
    final JavaContext context = javaContext;
    return new ForwardingAstVisitor() {

      @Override public boolean visitClassDeclaration(ClassDeclaration node) {
        // skip R2
        return R2.equals(node.astName().astValue());
      }

      @Override public boolean visitAnnotation(Annotation node) {
        // skip annotations
        return true;
      }

      @Override public boolean visitSelect(Select node) {
        return detectR2(context, node, node.astIdentifier());
      }

      @Override public boolean visitVariableReference(VariableReference node) {
        return detectR2(context, node, node.astIdentifier());
      }
    };
  }

  private static boolean detectR2(JavaContext context, Node node, Identifier identifier) {
    boolean isR2 = node.getParent() != null
        && (identifier.toString().equals(R2) || identifier.toString().contains(".R2."))
        && node.getParent() instanceof Select
        && SUPPORTED_TYPES.contains(((Select) node.getParent()).astIdentifier().toString());

    if (isR2 && !context.isSuppressedWithComment(node, ISSUE)) {
      context.report(ISSUE, node, context.getLocation(identifier), LINT_ERROR_BODY);
    }

    return isR2;
  }
}
