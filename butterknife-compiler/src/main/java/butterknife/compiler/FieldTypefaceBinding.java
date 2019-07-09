package butterknife.compiler;

import androidx.annotation.Nullable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

final class FieldTypefaceBinding implements ResourceBinding {
  private static final ClassName RESOURCES_COMPAT =
      ClassName.get("androidx.core.content.res", "ResourcesCompat");
  private static final ClassName TYPEFACE = ClassName.get("android.graphics", "Typeface");

  /** Keep in sync with {@link android.graphics.Typeface} constants. */
  enum TypefaceStyles {
    NORMAL(0),
    BOLD(1),
    ITALIC(2),
    BOLD_ITALIC(3);

    final int value;

    TypefaceStyles(int value) {
      this.value = value;
    }

    @Nullable static TypefaceStyles fromValue(int value) {
      for (TypefaceStyles style : values()) {
        if (style.value == value) {
          return style;
        }
      }
      return null;
    }
  }

  private final Id id;
  private final String name;
  private final TypefaceStyles style;

  FieldTypefaceBinding(Id id, String name, TypefaceStyles style) {
    this.id = id;
    this.name = name;
    this.style = style;
  }

  @Override public Id id() {
    return id;
  }

  @Override public boolean requiresResources(int sdk) {
    return sdk >= 26;
  }

  @Override public CodeBlock render(int sdk) {
    CodeBlock typeface = sdk >= 26
        ? CodeBlock.of("res.getFont($L)", id.code)
        : CodeBlock.of("$T.getFont(context, $L)", RESOURCES_COMPAT, id.code);
    if (style != TypefaceStyles.NORMAL) {
      typeface = CodeBlock.of("$1T.create($2L, $1T.$3L)", TYPEFACE, typeface, style);
    }
    return CodeBlock.of("target.$L = $L", name, typeface);
  }
}
