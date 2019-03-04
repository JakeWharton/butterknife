package butterknife.compiler;

import com.squareup.javapoet.CodeBlock;

import static butterknife.compiler.BindingSet.UTILS;

final class FieldAttrBinding implements ResourceBinding {

  enum Type {
    COLOR_INT {
      @Override
      CodeBlock render(String name, Id id) {
        return CodeBlock.of("target.$L = $T.getThemeColor(context, $L)", name, UTILS, id.code);
      }
    },
    COLOR_STATE_LIST {
      @Override
      CodeBlock render(String name, Id id) {
        return CodeBlock.of("target.$L = $T.getThemeColorStateList(context, $L)", name, UTILS,
           id.code);
      }
    };

    abstract CodeBlock render(String name, Id id);
  }

  private final Id id;
  private final String name;
  private final Type type;

  FieldAttrBinding(Id id, String name, Type type) {
    this.id = id;
    this.name = name;
    this.type = type;
  }

  @Override
  public Id id() {
    return id;
  }

  @Override
  public boolean requiresResources(int sdk) {
    return false;
  }

  @Override
  public CodeBlock render(int sdk) {
    return type.render(name, id);
  }
}