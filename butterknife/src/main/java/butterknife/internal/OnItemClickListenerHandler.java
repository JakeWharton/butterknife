package butterknife.internal;

import android.view.View;
import android.widget.AdapterView;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import static butterknife.internal.TargetClass.emitCastIfNeeded;

public class OnItemClickListenerHandler implements InjectableListenerHandler {
  private static final int TYPE_ADAPTER = 0;
  private static final int TYPE_VIEW = 1;
  private static final int TYPE_POSITION = 2;
  private static final int TYPE_ID = 3;
  private static final int PARAM_COUNT = 4;

  @Override
  public Param[] parseParamTypesAndValidateMethod(InjectViewProcessor ivp,
      ExecutableElement element) throws InjectableListenerException {
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
    List<? extends VariableElement> parameters = element.getParameters();
    if (parameters.isEmpty()) {
      return Param.NONE;
    }

    // Verify that there is only a single parameter.
    int paramCount = parameters.size();
    if (paramCount > PARAM_COUNT) {
      StringBuilder builder = new StringBuilder();
      appendFormattingMessage(builder, enclosingElement, element);
      throw new InjectableListenerException(element,
          "AdapterView parent, View view, int position, and long id. (%s.%s)",
          enclosingElement.getQualifiedName(), element.getSimpleName());
    }

    Param[] params = new Param[paramCount];
    ivp.findBestParameter(params, parameters, AdapterView.class, TYPE_ADAPTER);
    ivp.findBestParameter(params, parameters, View.class, TYPE_VIEW);
    ivp.findBestParameter(params, parameters, int.class, TYPE_POSITION);
    ivp.findBestParameter(params, parameters, long.class, TYPE_ID);

    if (hasNulls(params)) {
      StringBuilder builder = new StringBuilder();
      builder.append("Unable to match @OnItemClick method arguments.\n\n");
      for (int i = 0; i < params.length; i++) {
        builder.append("  Parameter #")
            .append(i)
            .append(' ');

        Param param = params[i];
        if (param == null) {
          builder.append("did not match");
        } else {
          builder.append('(')
              .append(param.type)
              .append(") matched listener parameter #")
              .append(param.listenerPosition);
        }
        builder.append('\n');
      }
      builder.append('\n');
      appendFormattingMessage(builder, enclosingElement, element);
      throw new InjectableListenerException(element, builder.toString());
    }

    return params;
  }

  private static boolean hasNulls(Param[] params) {
    for (Param param : params) {
      if (param == null) {
        return true;
      }
    }
    return false;
  }

  private static void appendFormattingMessage(StringBuilder builder, TypeElement enclosingElement,
      ExecutableElement element) {
    builder.append("@OnItemClick methods may only have up to four parameters (")
        .append(enclosingElement.getQualifiedName())
        .append('.')
        .append(element.getSimpleName())
        .append("):\n\n")
        .append("  AdapterView (parent view),\n")
        .append("  View (clicked view),\n")
        .append("  int (position),\n")
        .append("  long (id),\n\n")
        .append("These may be listed in any order but will be search for from top to bottom.");
  }

  @Override public void emit(StringBuilder builder, MethodBinding methodBinding) {
    builder.append("    ((android.widget.AdapterView<?>) view).setOnItemClickListener(\n")
        .append("      new android.widget.AdapterView.OnItemClickListener() {\n")
        .append("        @Override public void onItemClick(\n")
        .append("            android.widget.AdapterView<?> parent, View view, int position, ")
        .append("long id) {\n") // TODO WTF
        .append("          target.")
        .append(methodBinding.getName())
        .append('(');
    Param[] params = methodBinding.getParams();
    for (int i = 0; i < params.length; i++) {
      if (i > 0) {
        builder.append(", ");
      }
      Param param = params[i];
      String sourceType;
      String paramName;
      switch (param.listenerPosition) {
        case TYPE_ADAPTER:
          sourceType = "android.widget.AdapterView<?>";
          paramName = "parent";
          break;
        case TYPE_VIEW:
          sourceType = "android.view.View";
          paramName = "view";
          break;
        case TYPE_POSITION:
          sourceType = "int";
          paramName = "position";
          break;
        case TYPE_ID:
          sourceType = "long";
          paramName = "id";
          break;
        default:
          throw new IllegalStateException("Unknown position: " + param.listenerPosition);
      }
      emitCastIfNeeded(builder, sourceType, param.type);
      builder.append(paramName);
    }
    builder.append(");\n")
        .append("        }\n")
        .append("      });\n");
  }
}
