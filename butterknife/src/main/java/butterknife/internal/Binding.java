package butterknife.internal;

/** A field or method view injection binding. */
interface Binding {
  /** A description of the binding in human readable form (e.g., "field 'foo'"). */
  String getDescription();
  /** False if the {@link butterknife.Optional @Optional} annotation is present on the binding. */
  boolean isRequired();
}
