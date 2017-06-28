package butterknife.lint;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.LintUtils;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.google.common.collect.ImmutableSet;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiReferenceExpression;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Custom lint rule to make sure that generated R2 is not referenced outside annotations.
 */
public class InvalidR2UsageDetector extends Detector implements Detector.JavaPsiScanner {
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

  @Override public List<Class<? extends PsiElement>> getApplicablePsiTypes() {
    return Collections.<Class<? extends PsiElement>>singletonList(PsiClass.class);
  }

  @Override public JavaElementVisitor createPsiVisitor(final JavaContext context) {
    return new JavaElementVisitor() {
      @Override public void visitClass(PsiClass node) {
        node.accept(new R2UsageVisitor(context));
      }
    };
  }

  private static class R2UsageVisitor extends JavaRecursiveElementVisitor {
    private final JavaContext context;

    R2UsageVisitor(JavaContext context) {
      this.context = context;
    }

    @Override public void visitAnnotation(PsiAnnotation annotation) {
      // skip annotations
    }

    @Override public void visitReferenceExpression(PsiReferenceExpression expression) {
      detectR2(context, expression);
      super.visitReferenceExpression(expression);
    }

    private static void detectR2(JavaContext context, PsiElement node) {
      PsiClass[] classes = context.getJavaFile().getClasses();
      if (classes.length > 0 && classes[0].getName() != null) {
        String qualifiedName = classes[0].getName();
        if (qualifiedName.contains("_ViewBinder") || qualifiedName.contains("_ViewBinding")
            || qualifiedName.equals(R2)) {
          // skip generated files and R2
          return;
        }
      }
      boolean isR2 = isR2Expression(node);
      if (isR2 && !context.isSuppressedWithComment(node, ISSUE)) {
        context.report(ISSUE, node, context.getLocation(node), LINT_ERROR_BODY);
      }
    }

    private static boolean isR2Expression(PsiElement node) {
      if (node.getParent() == null) {
        return false;
      }
      String text = node.getText();
      PsiElement parent = LintUtils.skipParentheses(node.getParent());
      return (text.equals(R2) || text.contains(".R2"))
          && parent instanceof PsiExpression
          && endsWithAny(parent.getText(), SUPPORTED_TYPES);
    }

    private static boolean endsWithAny(String text, Set<String> possibleValues) {
      String[] tokens = text.split("\\.");
      return tokens.length > 1 && possibleValues.contains(tokens[tokens.length - 1]);
    }
  }
}
