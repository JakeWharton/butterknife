package butterknife.internal;

import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import static butterknife.internal.TargetClass.emitCastIfNeeded;

public class OnClickListenerHandler implements InjectableListenerHandler {
  @Override
  public String[] parseParamTypesAndValidateMethod(InjectViewProcessor ivp,
      ExecutableElement element)
      throws InjectableListenerException {
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
    List<? extends VariableElement> parameters = element.getParameters();
    if (parameters.isEmpty()) {
      return new String[0];
    }

    // Verify that there is only a single parameter.
    if (parameters.size() != 1) {
      throw new InjectableListenerException(element,
          "@OnClick methods may only have one parameter which is View or a subclass. (%s.%s)",
          enclosingElement.getQualifiedName(), element.getSimpleName());
    }
    // Verify that the parameter type extends from View.
    VariableElement variableElement = parameters.get(0);
    if (!ivp.isSubtypeOfView(variableElement.asType())) {
      throw new InjectableListenerException(element,
          "@OnClick method parameter type must be View or a subclass. (%s.%s)",
          enclosingElement.getQualifiedName(), element.getSimpleName());
    }

    return new String[] {variableElement.asType().toString()};
  }

  @Override public void emit(StringBuilder builder, MethodBinding methodBinding) {
    builder.append("    view.setOnClickListener(new View.OnClickListener() {\n")
        .append("      @Override public void onClick(View view) {\n")
        .append("        target.")
        .append(methodBinding.getName())
        .append('(');
    if (methodBinding.getParamTypes().length > 0) {
      // Only emit a cast if the type is not View.
      emitCastIfNeeded(builder, methodBinding.getParamTypes()[0]);
      builder.append("view");
    }
    builder.append(");\n")
        .append("      }\n")
        .append("    });\n");
  }
}
