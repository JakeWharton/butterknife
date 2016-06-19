package butterknife.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

/**
 * Represents an ID of an Android resource.
 */
final class Id {
  final int value;
  final CodeBlock code;

  Id(int value) {
    this.value = value;
    this.code = CodeBlock.of("$L", value);
  }

  Id(int value, ClassName className, String resourceName) {
    this.value = value;
    this.code = CodeBlock.of("$T.$N", className, resourceName);
  }

  @Override public boolean equals(Object o) {
    return o instanceof Id && value == ((Id) o).value;
  }

  @Override public int hashCode() {
    return value;
  }

  @Override public String toString() {
    return String.valueOf(value);
  }
}
