package butterknife.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

final class FieldResourceBinding {
  enum Type {
    BITMAP(BindingSet.BITMAP_FACTORY, "decodeResource", true),
    BOOL("getBoolean"),
    COLOR(BindingSet.CONTEXT_COMPAT, "getColor", false),
    COLOR_STATE_LIST(BindingSet.CONTEXT_COMPAT, "getColorStateList", false),
    DIMEN_AS_INT("getDimensionPixelSize"),
    DIMEN_AS_FLOAT("getDimension"),
    FLOAT(BindingSet.UTILS, "getFloat", false),
    INT("getInteger"),
    INT_ARRAY("getIntArray"),
    STRING("getString"),
    STRING_ARRAY("getStringArray"),
    TEXT_ARRAY("getTextArray"),
    TYPED_ARRAY("obtainTypedArray");

    final ClassName typeName;
    final String methodName;
    final boolean requiresResource;

    Type(String methodName) {
      this.typeName = null;
      this.methodName = methodName;
      this.requiresResource = true;
    }

    Type(ClassName typeName, String methodName, boolean requiresResource) {
      this.typeName = typeName;
      this.methodName = methodName;
      this.requiresResource = requiresResource;
    }
  }

  final Id id;
  final String name;
  final Type type;

  FieldResourceBinding(Id id, String name, Type type) {
    this.id = id;
    this.name = name;
    this.type = type;
  }

  CodeBlock render() {
    if (type.typeName == null) {
      return CodeBlock.of("target.$L = res.$L($L)", name, type.methodName, id.code);
    }
    if (type.requiresResource) {
      return CodeBlock.of("target.$L = $T.$L(res, $L)", name, type.typeName, type.methodName,
          id.code);
    }
    return CodeBlock.of("target.$L = $T.$L(context, $L)", name, type.typeName, type.methodName,
        id.code);
  }
}
