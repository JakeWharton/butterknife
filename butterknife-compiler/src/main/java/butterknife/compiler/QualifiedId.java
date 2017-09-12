package butterknife.compiler;

import javax.lang.model.element.PackageElement;

final class QualifiedId {
  final PackageElement packageName;
  final int id;

  QualifiedId(PackageElement packageName, int id) {
    this.packageName = packageName;
    this.id = id;
  }

  @Override public String toString() {
    return "QualifiedId{packageName='" + packageName + "', id=" + id + '}';
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof QualifiedId)) return false;
    QualifiedId other = (QualifiedId) o;
    return id == other.id
        && packageName.equals(other.packageName);
  }

  @Override public int hashCode() {
    int result = packageName.hashCode();
    result = 31 * result + id;
    return result;
  }
}
