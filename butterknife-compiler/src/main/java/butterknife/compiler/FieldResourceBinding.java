package butterknife.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

final class FieldResourceBinding implements ResourceBinding {
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
    final boolean requiresResources;

    Type(String methodName) {
      this.typeName = null;
      this.methodName = methodName;
      this.requiresResources = true;
    }

    Type(ClassName typeName, String methodName, boolean requiresResources) {
      this.typeName = typeName;
      this.methodName = methodName;
      this.requiresResources = requiresResources;
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

  @Override public Id id() {
    return id;
  }

  @Override public boolean requiresResources() {
    return type.requiresResources;
  }

  @Override public CodeBlock render() {
    if (type.typeName == null) {
      return CodeBlock.of("target.$L = res.$L($L)", name, type.methodName, id.code);
    }
    if (type.requiresResources) {
      return CodeBlock.of("target.$L = $T.$L(res, $L)", name, type.typeName, type.methodName,
          id.code);
    }
    return CodeBlock.of("target.$L = $T.$L(context, $L)", name, type.typeName, type.methodName,
        id.code);
  }
}
