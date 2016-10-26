package butterknife.compiler;

final class QualifiedId {
  final String packageName;
  final int id;

  QualifiedId(String packageName, int id) {
    this.packageName = packageName;
    this.id = id;
  }

  @Override public String toString() {
    return "QualifiedId{" +
        "packageName='" + packageName + '\'' +
        ", id=" + id +
        '}';
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    QualifiedId that = (QualifiedId) o;

    if (id != that.id) return false;
    if (!packageName.equals(that.packageName)) return false;

    return true;
  }

  @Override public int hashCode() {
    int result = packageName.hashCode();
    result = 31 * result + id;
    return result;
  }
}
