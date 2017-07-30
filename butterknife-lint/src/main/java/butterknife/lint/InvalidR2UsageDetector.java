package butterknife.lint;

import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.LintUtils;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.jetbrains.uast.UAnnotation;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UExpression;
import org.jetbrains.uast.UFile;
import org.jetbrains.uast.UQualifiedReferenceExpression;
import org.jetbrains.uast.USimpleNameReferenceExpression;
import org.jetbrains.uast.visitor.AbstractUastVisitor;

/**
 * Custom lint rule to make sure that generated R2 is not referenced outside annotations.
 */
public class InvalidR2UsageDetector extends Detector implements Detector.UastScanner {
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

  @Override public List<Class<? extends UElement>> getApplicableUastTypes() {
    return Collections.singletonList(UClass.class);
  }

  @Override public UElementHandler createUastHandler(final JavaContext context) {
    return new UElementHandler() {
      @Override public void visitClass(UClass node) {
        node.accept(new R2UsageVisitor(context));
      }
    };
  }

  private static class R2UsageVisitor extends AbstractUastVisitor {
    private final JavaContext context;

    R2UsageVisitor(JavaContext context) {
      this.context = context;
    }

    @Override public boolean visitAnnotation(UAnnotation annotation) {
      // skip annotations
      return true;
    }

    @Override public boolean visitQualifiedReferenceExpression(UQualifiedReferenceExpression node) {
      detectR2(context, node);
      return super.visitQualifiedReferenceExpression(node);
    }

    @Override
    public boolean visitSimpleNameReferenceExpression(USimpleNameReferenceExpression node) {
      detectR2(context, node);
      return super.visitSimpleNameReferenceExpression(node);
    }

    private static void detectR2(JavaContext context, UElement node) {
      UFile sourceFile = context.getUastFile();
      List<UClass> classes = sourceFile.getClasses();
      if (!classes.isEmpty() && classes.get(0).getName() != null) {
        String qualifiedName = classes.get(0).getName();
        if (qualifiedName.contains("_ViewBinder")
            || qualifiedName.contains("_ViewBinding")
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

    private static boolean isR2Expression(UElement node) {
      UElement parentNode = node.getUastParent();
      if (parentNode == null) {
        return false;
      }
      String text = node.asSourceString();
      UElement parent = LintUtils.skipParentheses(parentNode);
      return (text.equals(R2) || text.contains(".R2"))
          && parent instanceof UExpression
          && endsWithAny(parent.asSourceString(), SUPPORTED_TYPES);
    }

    private static boolean endsWithAny(String text, Set<String> possibleValues) {
      String[] tokens = text.split("\\.");
      return tokens.length > 1 && possibleValues.contains(tokens[tokens.length - 1]);
    }
  }
}
