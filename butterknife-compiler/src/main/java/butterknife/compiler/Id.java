package butterknife.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

/**
 * Represents an ID of an Android resource.
 */
final class Id {
  private static final ClassName ANDROID_R = ClassName.get("android", "R");

  final int value;
  final CodeBlock code;
  final boolean qualifed;

  Id(int value) {
    this.value = value;
    this.code = CodeBlock.of("$L", value);
    this.qualifed = false;
  }

  Id(int value, ClassName className, String resourceName) {
    this.value = value;
    this.code = className.topLevelClassName().equals(ANDROID_R)
      ? CodeBlock.of("$L.$N", className, resourceName)
      : CodeBlock.of("$T.$N", className, resourceName);
    this.qualifed = true;
  }

  @Override public String toString() {
    throw new UnsupportedOperationException("Please use value or code explicitly");
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Id id = (Id) o;

    if (value != id.value) return false;
    if (qualifed != id.qualifed) return false;
    if (!code.equals(id.code)) return false;

    return true;
  }

  @Override public int hashCode() {
    int result = value;
    result = 31 * result + code.hashCode();
    result = 31 * result + (qualifed ? 1 : 0);
    return result;
  }
}
