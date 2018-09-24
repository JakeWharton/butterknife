ButterKnife Reflect
===================

The `butterknife-reflect` artifact is an API-compatible replacement for `butterknife` which uses
100% reflection to fulfill field and method bindings for use during development.


Er, what? Why would I want this?
--------------------------------

The normal `butterknife` artifact requires the use of `butterknife-compiler` as an annotation
processor for compile-time validation of your bindings and code generation for runtime performance.
This is a desirable feature for your CI and release builds, but it slows down iterative development.
By using `butterknife-reflect` for only your IDE builds, you have one less annotation processor
sitting between you and your running app. This is especially important for Kotlin-only or
Java/Kotlin mixed projects using KAPT. And if `butterknife-compiler` is your only annotation
processor for a module, using `butterknife-reflect` means that **zero** annotation processors run
during development.


Can I use this in production?
-----------------------------

No.

Well technically you _can_, but don't. It's slow, inefficient, and lacks the level of validation
that normal Butter Knife usage provides.


Usage
-----

Kotlin modules:
```groovy
dependencies {
  if (properties.containsKey('android.injected.invoked.from.ide')) {
    implementation 'com.jakewharton:butterknife-reflect:<version>'
  } else {
    implementation 'com.jakewharton:butterknife:<version>'
    kapt 'com.jakewharton:butterknife-compiler:<version>'
  }
}
```

Java modules:
```groovy
dependencies {
  if (properties.containsKey('android.injected.invoked.from.ide')) {
    implementation 'com.jakewharton:butterknife-reflect:<version>'
  } else {
    implementation 'com.jakewharton:butterknife:<version>'
    annotationProcessor 'com.jakewharton:butterknife-compiler:<version>'
  }
}
```

_(Replacing `<version>` with whatever version you are using.)_

If you have a dedicated variant for development you can skip the `if` check and simply add
`butterknife-reflect` to that variant and `butterknife`+`butterknife-compiler` for the regular
variants.
