package butterknife.compiler;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.util.List;

import static butterknife.compiler.BindingSet.UTILS;
import static butterknife.compiler.BindingSet.requiresCast;

final class FieldCollectionViewBinding {
  enum Kind {
    ARRAY("arrayOf"),
    LIST("listOf");

    final String factoryName;

    Kind(String factoryName) {
      this.factoryName = factoryName;
    }
  }

  final String name;
  private final TypeName type;
  private final Kind kind;
  private final boolean required;
  private final List<Id> ids;

  FieldCollectionViewBinding(String name, TypeName type, Kind kind, List<Id> ids,
      boolean required) {
    this.name = name;
    this.type = type;
    this.kind = kind;
    this.ids = ids;
    this.required = required;
  }

  CodeBlock render() {
    CodeBlock.Builder builder = CodeBlock.builder()
        .add("target.$L = $T.$L(", name, UTILS, kind.factoryName);
    for (int i = 0; i < ids.size(); i++) {
      if (i > 0) {
        builder.add(", ");
      }
      builder.add("\n");

      boolean requiresCast = requiresCast(type);
      if (!requiresCast && !required) {
        builder.add("source.findViewById($L)", ids.get(i).code);
      } else {
        builder.add("$T.find", UTILS);
        builder.add(required ? "RequiredView" : "OptionalView");
        if (requiresCast) {
          builder.add("AsType");
        }
        builder.add("(source, $L, \"field '$L'\"", ids.get(i).code, name);
        if (requiresCast) {
          TypeName rawType = type;
          if (rawType instanceof ParameterizedTypeName) {
            rawType = ((ParameterizedTypeName) rawType).rawType;
          }
          builder.add(", $T.class", rawType);
        }
        builder.add(")");
      }
    }
    return builder.add(")").build();
  }
}
