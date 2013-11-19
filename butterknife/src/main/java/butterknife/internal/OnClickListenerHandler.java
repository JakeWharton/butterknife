package butterknife.internal;

import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import static butterknife.internal.InjectViewProcessor.isSubclassOfView;
import static butterknife.internal.TargetClass.emitCastIfNeeded;

public class OnClickListenerHandler implements InjectableListenerHandler {
  @Override
  public Param[] parseParamTypesAndValidateMethod(InjectViewProcessor ivp,
      ExecutableElement element)
      throws InjectableListenerException {
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
    List<? extends VariableElement> parameters = element.getParameters();
    if (parameters.isEmpty()) {
      return Param.NONE;
    }

    // Verify that there is only a single parameter.
    if (parameters.size() != 1) {
      throw new InjectableListenerException(element,
          "@OnClick methods may only have one parameter which is View or a subclass. (%s.%s)",
          enclosingElement.getQualifiedName(), element.getSimpleName());
    }
    // Verify that the parameter type extends from View.
    VariableElement variableElement = parameters.get(0);
    if (!isSubclassOfView(variableElement.asType())) {
      throw new InjectableListenerException(element,
          "@OnClick method parameter type must be View or a subclass. (%s.%s)",
          enclosingElement.getQualifiedName(), element.getSimpleName());
    }

    return new Param[] {
        new Param(0 /* unused */, variableElement.asType().toString())
    };
  }

  @Override public void emit(StringBuilder builder, MethodBinding methodBinding) {
    builder.append("    view.setOnClickListener(new View.OnClickListener() {\n")
        .append("      @Override public void onClick(View view) {\n")
        .append("        target.")
        .append(methodBinding.getName())
        .append('(');
    if (methodBinding.getParams().length > 0) {
      emitCastIfNeeded(builder, methodBinding.getParams()[0].type);
      builder.append("view");
    }
    builder.append(");\n")
        .append("      }\n")
        .append("    });\n");
  }
}
