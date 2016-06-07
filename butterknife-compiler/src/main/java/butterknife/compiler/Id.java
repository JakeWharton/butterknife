package butterknife.compiler;

/**
 * Represents an ID of an Android resource.
 */
final class Id {

  private final int intId;
  private final String var;

  Id(int intId) {
    this(intId, String.valueOf(intId));
  }

  Id(int intId, String var) {
    this.intId = intId;
    this.var = var;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Id id = (Id) o;

    return intId == id.intId;
  }

  @Override public int hashCode() {
    return intId;
  }

  @Override public String toString() {
    return var;
  }

  int getIntId() {
    return intId;
  }
}
