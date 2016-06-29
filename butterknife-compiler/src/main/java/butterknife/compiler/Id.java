package butterknife.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

/**
 * Represents an ID of an Android resource.
 */
final class Id {
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
    this.code = CodeBlock.of("$T.$N", className, resourceName);
    this.qualifed = true;
  }

  @Override public boolean equals(Object o) {
    return o instanceof Id && value == ((Id) o).value;
  }

  @Override public int hashCode() {
    return value;
  }

  @Override public String toString() {
    throw new UnsupportedOperationException("Please use value or code explicitly");
  }
}
