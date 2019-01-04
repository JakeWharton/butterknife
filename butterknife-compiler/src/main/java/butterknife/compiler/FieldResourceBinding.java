package butterknife.compiler;

import androidx.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class FieldResourceBinding implements ResourceBinding {
  enum Type {
    BITMAP(new ResourceMethod(BindingSet.BITMAP_FACTORY, "decodeResource", true, 1)),
    BOOL("getBoolean"),
    COLOR(new ResourceMethod(BindingSet.CONTEXT_COMPAT, "getColor", false, 1),
        new ResourceMethod(null, "getColor", false, 23)),
    COLOR_STATE_LIST(new ResourceMethod(BindingSet.CONTEXT_COMPAT,
        "getColorStateList", false, 1),
        new ResourceMethod(null, "getColorStateList", false, 23)),
    DIMEN_AS_INT("getDimensionPixelSize"),
    DIMEN_AS_FLOAT("getDimension"),
    FLOAT(new ResourceMethod(BindingSet.UTILS, "getFloat", false, 1)),
    INT("getInteger"),
    INT_ARRAY("getIntArray"),
    STRING("getString"),
    STRING_ARRAY("getStringArray"),
    TEXT_ARRAY("getTextArray"),
    TYPED_ARRAY("obtainTypedArray");

    private final ImmutableList<ResourceMethod> methods;

    Type(ResourceMethod... methods) {
      List<ResourceMethod> methodList = new ArrayList<>(methods.length);
      Collections.addAll(methodList, methods);
      Collections.sort(methodList);
      Collections.reverse(methodList);
      this.methods = ImmutableList.copyOf(methodList);
    }

    Type(String methodName) {
      methods = ImmutableList.of(new ResourceMethod(null, methodName, true, 1));
    }

    ResourceMethod methodForSdk(int sdk) {
      for (ResourceMethod method : methods) {
        if (method.sdk <= sdk) {
          return method;
        }
      }
      throw new AssertionError();
    }
  }

  @Immutable
  static final class ResourceMethod implements Comparable<ResourceMethod> {
    @SuppressWarnings("Immutable")
    final @Nullable ClassName typeName;
    final String name;
    final boolean requiresResources;
    final int sdk;

    ResourceMethod(@Nullable ClassName typeName, String name, boolean requiresResources, int sdk) {
      this.typeName = typeName;
      this.name = name;
      this.requiresResources = requiresResources;
      this.sdk = sdk;
    }

    @Override public int compareTo(ResourceMethod other) {
      return Integer.compare(sdk, other.sdk);
    }
  }

  private final Id id;
  private final String name;
  private final Type type;

  FieldResourceBinding(Id id, String name, Type type) {
    this.id = id;
    this.name = name;
    this.type = type;
  }

  @Override public Id id() {
    return id;
  }

  @Override public boolean requiresResources(int sdk) {
    return type.methodForSdk(sdk).requiresResources;
  }

  @Override public CodeBlock render(int sdk) {
    ResourceMethod method = type.methodForSdk(sdk);
    if (method.typeName == null) {
      if (method.requiresResources) {
        return CodeBlock.of("target.$L = res.$L($L)", name, method.name, id.code);
      }
      return CodeBlock.of("target.$L = context.$L($L)", name, method.name, id.code);
    }
    if (method.requiresResources) {
      return CodeBlock.of("target.$L = $T.$L(res, $L)", name, method.typeName, method.name,
          id.code);
    }
    return CodeBlock.of("target.$L = $T.$L(context, $L)", name, method.typeName, method.name,
        id.code);
  }
}
