package butterknife.internal;

/** Represents a method parameter and some type of identifier. */
class Param {
  static final Param[] NONE = new Param[0];

  final int listenerPosition;
  final String type;

  Param(int listenerPosition, String type) {
    this.listenerPosition = listenerPosition;
    this.type = type;
  }
}
