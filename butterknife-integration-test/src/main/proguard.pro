-dontoptimize
-dontobfuscate

# STUFF USED BY TESTS:

-keep class butterknife.internal.Utils {
  <methods>;
}

-keep class butterknife.Unbinder {
  void unbind();
}

-keep class com.example.butterknife.unbinder.H {
  <init>(...);
}
