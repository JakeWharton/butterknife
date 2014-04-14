package butterknife.internal;

/** A field or method view injection binding. */
interface Binding {
  /** A description of the binding in human readable form (e.g., "field 'foo'"). */
  String getDescription();
}
