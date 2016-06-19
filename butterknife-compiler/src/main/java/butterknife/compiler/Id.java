package butterknife.compiler;

import com.squareup.javapoet.ClassName;
import java.util.Objects;

/**
 * Represents an ID of an Android resource.
 */
interface Id {

  /**
   * A final ID.
   */
  final class Constant implements Id {

    private final int id;

    Constant(int id) {
      this.id = id;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Constant constant = (Constant) o;
      return id == constant.id;
    }

    @Override
    public int hashCode() {
      return Objects.hash(id);
    }

    @Override
    public String toString() {
      return String.valueOf(id);
    }

    public int getId() {
      return id;
    }
  }

  /**
   * A non-final ID (produced by library projects).
   */
  final class Reference implements Id {

    private final ClassName idClassName;
    private final String idName;

    Reference(ClassName idClassName, String idName) {
      this.idClassName = idClassName;
      this.idName = idName;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Reference reference = (Reference) o;
      return Objects.equals(idClassName, reference.idClassName)
          && Objects.equals(idName, reference.idName);
    }

    @Override
    public int hashCode() {
      return Objects.hash(idClassName, idName);
    }

    @Override
    public String toString() {
      return idClassName + "." + idName;
    }
  }
}
