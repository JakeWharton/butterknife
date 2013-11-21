package butterknife.internal;

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

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
  static Listener from(Class<?> listenerClass) {
    Method[] listenerDeclaredMethods = listenerClass.getDeclaredMethods();
    if (listenerDeclaredMethods.length != 1) {
      throw new IllegalArgumentException(
          listenerClass.getSimpleName() + " is not a single-method interface");
    }
    Method listenerMethod = listenerDeclaredMethods[0];

    String ownerType = listenerClass.getDeclaringClass().getCanonicalName();
    String setterName = "set" + listenerClass.getSimpleName();
    String type = listenerClass.getCanonicalName();
    String methodName = listenerMethod.getName();
    String returnType = listenerMethod.getReturnType().getCanonicalName(); // Assuming simple type.

    Class<?>[] listenerParameterTypes = listenerMethod.getParameterTypes();
    List<String> parameterTypes = new ArrayList<String>(listenerParameterTypes.length);
    for (Class<?> listenerParameterType : listenerParameterTypes) {
      StringBuilder builder = new StringBuilder(listenerParameterType.getCanonicalName());
      TypeVariable<? extends Class<?>>[] typeParameters = listenerParameterType.getTypeParameters();
      if (typeParameters.length > 0) {
        builder.append('<');
        for (int i = 0; i < typeParameters.length; i++) {
          if (i > 0) {
            builder.append(',');
          }
          builder.append('?');
        }
        builder.append('>');
      }
      String parameterType = builder.toString();
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
