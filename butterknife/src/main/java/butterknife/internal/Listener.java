package butterknife.internal;

import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * Represents all the data needed to generate code for constructing a listener instance.
 * <p>
 * The values roughly correspond to the following listener template.
 * <pre><code>
 * public class OWNER_TYPE {
 *   public interface TYPE {
 *     RETURN_TYPE METHOD_NAME(PARAMETER_TYPE p0, ...);
 *   }
 * }
 * </code></pre>
 * }
 */
final class Listener {
  /**
   * Parse a {@link Class} into a {@link Listener}.
   *
   * @throws IllegalArgumentException if the method cannot be parsed into a {@link Listener}.
   */
  static Listener from(TypeElement listenerElement, Types typeUtils) {
    List<? extends Element> listenerEnclosedElements = listenerElement.getEnclosedElements();
    if (listenerEnclosedElements.size() != 1) {
      throw new IllegalArgumentException(
          listenerElement.getSimpleName() + " is not a single-method interface");
    }
    ExecutableElement listenerMethod = (ExecutableElement) listenerEnclosedElements.get(0);

    TypeMirror ownerTypeMirror = listenerElement.getEnclosingElement().asType();
    ownerTypeMirror = typeUtils.erasure(ownerTypeMirror);
    String ownerType = stripTypeParameters(ownerTypeMirror.toString());
    String setterName = "set" + listenerElement.getSimpleName();
    String type = listenerElement.getQualifiedName().toString();
    String methodName = listenerMethod.getSimpleName().toString();
    String returnType = listenerMethod.getReturnType().toString(); // Assuming simple type.

    List<? extends VariableElement> listenerParameterTypes = listenerMethod.getParameters();
    List<String> parameterTypes = new ArrayList<String>(listenerParameterTypes.size());
    for (VariableElement listenerParameterType : listenerParameterTypes) {
      String parameterType = listenerParameterType.asType().toString();
      if (parameterType.startsWith("java.lang.") && !parameterType.substring(10).contains(".")) {
        parameterType = parameterType.substring(10);
      }
      parameterTypes.add(parameterType);
    }

    return new Listener(ownerType, setterName, type, returnType, methodName, parameterTypes);
  }

  private final String ownerType;
  private final String setterName;
  private final String type;
  private final String returnType;
  private final String methodName;
  private final List<String> parameterTypes;

  private Listener(String ownerType, String setterName, String type, String returnType,
      String methodName, List<String> parameterTypes) {
    this.ownerType = ownerType;
    this.setterName = setterName;
    this.type = type;
    this.returnType = returnType;
    this.methodName = methodName;
    this.parameterTypes = parameterTypes; // No defensive copy, only instantiated internally.
  }

  // When built with Eclipse, the generated listener code contains an errant type parameter.
  private static String stripTypeParameters(String type) {
    int typeParamStart = type.indexOf('<');
    if (typeParamStart != -1) {
      return type.substring(0, typeParamStart);
    } else {
      return type;
    }
  }

  public String getOwnerType() {
    return ownerType;
  }

  public String getSetterName() {
    return setterName;
  }

  String getType() {
    return type;
  }

  String getReturnType() {
    return returnType;
  }

  String getMethodName() {
    return methodName;
  }

  List<String> getParameterTypes() {
    return parameterTypes;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Listener listener = (Listener) o;

    return methodName.equals(listener.methodName)
        && ownerType.equals(listener.ownerType)
        && parameterTypes.equals(listener.parameterTypes)
        && returnType.equals(listener.returnType)
        && setterName.equals(listener.setterName)
        && type.equals(listener.type);
  }

  @Override public int hashCode() {
    int result = ownerType.hashCode();
    result = 31 * result + setterName.hashCode();
    result = 31 * result + type.hashCode();
    result = 31 * result + returnType.hashCode();
    result = 31 * result + methodName.hashCode();
    result = 31 * result + parameterTypes.hashCode();
    return result;
  }
}
