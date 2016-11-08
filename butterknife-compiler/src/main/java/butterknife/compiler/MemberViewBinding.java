package butterknife.compiler;

/** A field or method view binding. */
interface MemberViewBinding {
  /** A description of the binding in human readable form (e.g., "field 'foo'"). */
  String getDescription();
}
