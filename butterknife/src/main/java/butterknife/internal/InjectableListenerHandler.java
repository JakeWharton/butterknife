package butterknife.internal;

import javax.lang.model.element.ExecutableElement;

/** Code that knows how to validate, parse, and emit code for a listener annotation. */
public interface InjectableListenerHandler {
  /**
   * Parse the supplied elements for the types it takes. Validate the types are applicable to the
   * associated annotation.
   *
   * @throws InjectableListenerException if the method is not properly formed.
   */
  String[] parseParamTypesAndValidateMethod(InjectViewProcessor ivp, ExecutableElement element)
      throws InjectableListenerException;

  /**
   * Emit necessary binding code to set up listener. Assume you have a non-null view in the {@code
   * view} variable. Code should be indented six spaces.
   */
  void emit(StringBuilder builder, MethodBinding method);
}
