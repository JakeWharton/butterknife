Change Log
==========

Version 10.2.3 *(2020-08-12)*
-----------------------------

Heads up: Development on this tool is winding down as [view binding](https://developer.android.com/topic/libraries/view-binding) is stable in AS/AGP 3.6+.

 * Fix: Support receiving `MotionEvent` in an `@OnTouch` callback when using 'butterknife-reflect'.


Version 10.2.2 *(2020-08-03)*
-----------------------------

Heads up: Development on this tool is winding down as [view binding](https://developer.android.com/topic/libraries/view-binding) is stable in AS/AGP 3.6+.

 * Fix: Views detached while processing click callbacks will no longer disable future clicks on other views.


Version 10.2.1 *(2019-12-19)*
-----------------------------

Heads up: Development on this tool is winding down as [view binding](https://developer.android.com/topic/libraries/view-binding) will be stable in AS/AGP 3.6.

 * New: Make R2-generating Gradle task cacheable by default.
 * Fix: R2 classes now generate their own unique values for entries. This ensures that the annotation processor
   can always do a reverse mapping from ID back to name and type. In AGP 3.6.0, the `R.txt` symbol table that was
   previously used as a source for values now uses 0 for every entry which required this change.
 * Fix: Lint check for R2 values now properly handles static imports for entries.


Version 10.2.0 *(2019-09-12)*
-----------------------------

 * New: Support incremental annotation processing.
 * Fix: Detect generated superclass bindings across compilation units.
 * Fix: Avoid deprecated APIs from the Android Gradle plugin. As a result, the new minimum supported version
   of the Android Gradle plugin is 3.3.


Version 10.1.0 *(2019-02-13)*
-----------------------------

 * New: Listeners which require return values (e.g., long click) can now be bound to methods returning `void`.
   The default value of `true` will be returned in this case.
 * New: Add support for `@OnTextChanged` and `@OnPageChange` to reflection backend.
 * Remove enforcement of required views in the reflection backend. Most `@Nullable` annotations do not have
   runtime retention so they can't be checked at runtime with reflection. Instead of forcing everyone to find
   a new annotation, this enforcement is now dropped. While this might lead to nulls in otherwise required
   view bindings, they'll either be unused or quickly cause a `NullPointerException`.


Version 10.0.0 *(2019-01-03)*
-----------------------------

 * Equivalent to 9.0.0 but only supports AndroidX-enabled builds.
 * Removed APIs deprecated in 9.0.0.


Version 9.0.0 *(2019-01-03)*
----------------------------

 * New: Support for AndroidX. Requires `android.useAndroidX=true` in `gradle.properties` to generate
   AndroidX code.

 * New: A `butterknife-runtime` artifact has been extracted from `butterknife` which contains the APIs
   required for the generated code but does not contain the code to reflectively look up the generated
   code. This allows you to reference the generated code directly such that R8/ProGuard optimization can
   rename both the generated code and your classes. `ButterKnife.bind` and the consumer R8/ProGuard rules
   remain in the old `butterknife` artifact.
 
 * New: Experimental `butterknife-reflect` artifact eliminates the need to run the annotation
   processor for IDE builds. This artifact is binary compatible with `butterknife` so it can be interchanged
   depending on how your build is being invoked. See [its README](butterknife-reflect/README.md) for more
   information. Currently about 90% of functionality is covered. File bugs for anything that does not work.

   Note: This artifact requires Java 8. There's no good reason for this except to push the ecosystem to
   having this be a default. As of AGP 3.2 there is no reason not to do this.

 * New: Lint checks have been ported to UAST and now work on Kotlin code.
 
 * Helpers such as `apply` have been deprecated on `ButterKnife` and are now available on the `ViewCollections` class.

 * Add support for Android Gradle plugin 3.3 and newer where `R` is no longer generated as Java source. This
   has a side-effect of removing support for Android Gradle plugin 3.0.x (and older).
 * Use Java 8 bytecode for all artifacts as announced in RC1 release notes.
 * Fix: Allow `@BindFont` to work prior to API 26 using `ResourcesCompat`.
 * Fix: Update Android Gradle plugin to 3.1 or newer to fix binary incompatibilities.
 * Fix: Correct generated resource annotation names when running Turkish locale.
 * Fix: Use the application ID instead of the resource package for generating `R2`.
 * Cache the fact that a class hierarchy has no remaining bindings to prevent traversing the hierarchy
   multiple times.
 * Deprecated methods from 8.x have been removed.


Version 9.0.0-rc3 *(2018-12-20)*
--------------------------------

 * Fix: Correct generated resource annotation names when running Turkish locale.
 * Cache the fact that a class hierarchy has no remaining bindings to prevent traversing the hierarchy
   multiple times.


Version 9.0.0-rc2 *(2018-11-19)*
--------------------------------

 * Add support for Android Gradle plugin 3.3 and newer where `R` is no longer generated as Java source. This
   has a side-effect of removing support for Android Gradle plugin 3.0.x (and older).
 * Use Java 8 bytecode for all artifacts as announced in RC1 release notes.


Version 9.0.0-rc1 *(2018-10-10)*
--------------------------------

 * New: Support for AndroidX. Requires `android.useAndroidX=true` in `gradle.properties` to generate
   AndroidX code.

 * New: A `butterknife-runtime` artifact has been extracted from `butterknife` which contains the APIs
   required for the generated code but does not contain the code to reflectively look up the generated
   code. This allows you to reference the generated code directly such that R8/ProGuard optimization can
   rename both the generated code and your classes. `ButterKnife.bind` and the consumer R8/ProGuard rules
   remain in the old `butterknife` artifact.
 
 * New: Experimental `butterknife-reflect` artifact eliminates the need to run the annotation
   processor for IDE builds. This artifact is binary compatible with `butterknife` so it can be interchanged
   depending on how your build is being invoked. See [its README](butterknife-reflect/README.md) for more
   information. Currently about 90% of functionality is covered. File bugs for anything that does not work.

   Note: This artifact requires Java 8. There's no good reason for this except to push the ecosystem to
   having this be a default. As of AGP 3.2 there is no reason not to do this.

 * New: Lint checks have been ported to UAST and now work on Kotlin code.

 * Fix: Allow `@BindFont` to work prior to API 26 using `ResourcesCompat`.
 * Fix: Update Android Gradle plugin to 3.1 or newer to fix binary incompatibilities.
 * Fix: Use the application ID instead of the resource package for generating `R2`.
 * Deprecated methods from 8.x have been removed.

Note: The next release candidate will switch all artifacts to require Java 8 bytecode which will force
your applications to enable Java 8 bytecode. As of AGP 3.2 there is no cost to this, and there is no
reason to have it set any lower.


Version 8.8.1 *(2017-08-09)*
----------------------------

 * Fix: Properly emit casts for single-bound view subtypes when `butterknife.debuggable` is set to `false`.


Version 8.8.0 *(2017-08-04)*
----------------------------

 * New: Processor option `butterknife.debuggable` controls whether debug information is generated. When
   specified as `false`, checks for required views being non-null are elided and casts are no longer guarded
   with user-friendly error messages. This reduces the amount of generated code for release builds at the
   expense of less friendly exceptions when something breaks.
 * Deprecate the `findById` methods. Compile against API 26 and use the normal `findViewById` for the same
   functionality.
 * Fix: Correct `@BindFont` code generation on pre-API 26 builds to pass a `Context` (not a `Resources`) to
   `ResourceCompat`.


Version 8.7.0 *(2017-07-07)*
----------------------------

 * New: `@BindFont` annotation binds `Typeface` instances with an optional style. Requires support libraries
   26.0.0-beta1 or newer.
 * New: `@BindAnim` annotation binds `Animation` instances.
 * New: Generate `R2` constants for animation, layout, menu, plurals, styles, and styleables.
 * Fix: Properly catch and re-throw type cast exceptions when method binding arguments do not match.


Version 8.6.0 *(2017-05-16)*
----------------------------

 * Plugin was ported to Kotlin and updated to support future Android Gradle plugin versions.
 * Fix: Properly handle multiple library modules using Butter Knife and defining the same ID.
 * Fix: Use the same classloader of the binding target to load the generated view binding class.


Version 8.5.1 *(2017-01-24)*
----------------------------

 * Fix: Tweak bundled ProGuard rules to only retain the two-argument constructor accessed via reflection.


Version 8.5.0 *(2017-01-23)*
----------------------------

 * Emit `@SuppressLint` when using `@OnTouch` to avoid a lint warning.
 * Migrate lint checks from Lombok AST to JetBrains PSI.
 * Annotations are no longer claimed by the processor.
 * Based on the minimum SDK version (as specified by `butterknife.minSdk` until http://b.android.com/187527 is
   released) the generated code now changes to use newer APIs when available.
 * Generated classes now include single-argument overloads for `View`, `Activity`, and `Dialog` subclasses.
 * Generated classes are no longer generic.
 * Minimum supported SDK is now 9.


Version 8.4.0 *(2016-08-26)*
----------------------------

 * New: `@BindFloat` annotation for dimensions whose format is of type 'float'. See the annotation for more
   information.
 * Generated constructors are now annotated with `@UiThread` and non-final, base classes `unbind()` methods
   are annotated with `@CallSuper`.


Version 8.3.0 *(2016-08-23)*
----------------------------

 * New: Support for Jack compiler in application projects.
 * Fix: Generate ~20% less code and ~40% less methods.
 * Fix: Allow `@BindView` to reference types which are generated by other annotation processors.
 * Experimental: The generated view binding class can now be used directly. This allows ProGuard shrinking,
   optimization, and obfuscation to work without any rules being needed. For a class `Test`, the binding
   class will be named `Test_ViewBinding`. Calling its constructor will bind the instance passed in, and
   the create object is also the implementation of `Unbinder` that can be used to unbind the views.
   Note: The API of this generated code is subject to backwards-incompatible changes until v9.0.


Version 8.2.1 *(2016-07-11)*
----------------------------

 * Fix: Do not emit `android.R` imports in generated code.
 * Fix: Ensure the processor does not crash when scanning for `R` classes. This can occur when used in a
   Kotlin project.


Version 8.2.0 *(2016-07-10)*
----------------------------

 * New: Support for library projects. Requires application of a Butter Knife Gradle plugin. See README for
   details.
 * New: Generated code now emits `R` references instead of raw integer IDs.
 * Fix: `@OnPageChange` listener binding now uses the 'add'/'remove' methods on `ViewPager` instead of 'set'.


Version 8.1.0 *(2016-06-14)*
----------------------------

 * New: Change the structure of generated view binders to optimize for performance and generated code. This
   should result in faster binding (not that it's slow) and a reduction of methods.
 * Fix: Call the correct method on `TextView` to unbind `@OnTextChanged` uses.
 * Fix: Properly handle package names which contain uppercase letters.


Version 8.0.1 *(2016-04-27)*
----------------------------

 * Fix: ProGuard rules now prevent obfuscation of only types which reference ButterKnife annotations.
 * Eliminate some of the generated machinery when referenced from `final` types.


Version 8.0.0 *(2016-04-25)*
----------------------------

 *  `@Bind` becomes `@BindView` and `@BindViews` (one view and multiple views, respectively).
 *  Calls to `bind` now return an `Unbinder` instance which can be used to `null` references. This replaces
    the `unbind` API and adds support for being able to clear listeners.
 *  New: `@BindArray` binds `String`, `CharSequence`, and `int` arrays and `TypeArray` to fields.
 *  New: `@BindBitmap` binds `Bitmap` instances from resources to fields.
 *  `@BindDrawable` now supports a `tint` field which accepts a theme attribute.
 *  The runtime and compiler are now split into two artifacts.

    ```groovy
    compile 'com.jakewharton:butterknife:8.0.0'
    apt 'com.jakewharton:butterknife-compiler:8.0.0'
    ```
 *  New: `apply` overloads which accept a single view and arrays of views.
 *  ProGuard rules now ship inside of the library and are included automatically.
 *  `@Optional` annotation is back to mark methods as being optional.


Version 7.0.1 *(2015-06-30)*
----------------------------

 * Fix: Correct `ClassCastException` which occurred when `@Nullable` array bindings had missing views.


Version 7.0.0 *(2015-06-27)*
----------------------------

 * `@Bind` replaces `@InjectView` and `@InjectViews`.
 * `ButterKnife.bind` and `ButterKnife.unbind` replaces `ButterKnife.inject` and `ButterKnife.reset`, respectively.
 * `@Optional` has been removed. Use `@Nullable` from the 'support-annotations' library, or any other annotation
   named "Nullable".
 * New: Resource binding annotations!
   * `@BindBool` binds an `R.bool` ID to a `boolean` field.
   * `@BindColor` binds an `R.color` ID to an `int` or `ColorStateList` field.
   * `@BindDimen` binds an `R.dimen` ID to an `int` (for pixel size) or `float` (for exact value) field.
   * `@BindDrawable` binds an `R.drawable` ID to a `Drawable` field.
   * `@BindInt` binds an `R.int` ID to an `int` field.
   * `@BindString` binds an `R.string` ID to a `String` field.
 * Fix: Missing views will be filtered out from list and array bindings.
 * Note: If you are using Proguard, the generated class name has changed from being suffixed with `$$ViewInjector`
   to `$$ViewBinder`.


Version 6.1.0 *(2015-01-29)*
----------------------------

 * New: Support for injecting interface types everywhere that views were previously supported (e.g., `Checkable`).
 * Eliminate reflection-based method invocation for injection and resetting. This makes performance slightly faster
   (although if you are worried about the performance of Butter Knife you have other problems). The only reflection
   in the library is a single `Class.forName` lookup for each type.


Version 6.0.0 *(2014-10-27)*
----------------------------

 * New: Listeners can bind to the root view being injected by omitting a view ID on the annotation.
 * New: Exceptions thrown from missing views now include the human-readable ID name (e.g., 'button1').
 * Specifying multiple fields binding to the same ID is now considered an error.
 * `findById` overload for view lookup on `Dialog` instances.
 * Experimental: Click listeners are now globally debounced per frame. This means that only a single click
   will be processed per frame preventing race conditions due to queued input events.
 * Experimental: Multiple methods can bind to the same listener provided that listener's callback method
   does not require a return value.


Version 5.1.2 *(2014-08-01)*
----------------------------

 * Report an error if the annotations are on a class inside the `android.*` or `java.*`
   package. Since we ignore these packages in the runtime, injection would never work.


Version 5.1.1 *(2014-06-19)*
----------------------------

 * Fix: Correct rare `ClassCastException` when unwinding an `InvocationTargetException`.


Version 5.1.0 *(2014-05-20)*
----------------------------

 * New listener!
   * `View`: `@OnTouch`.
 * Fix: `@Optional` now correctly works for `@InjectViews` fields.
 * Fix: Correct erasure problem which may have prevented the processor from running in Eclipse.


Version 5.0.1 *(2014-05-04)*
----------------------------

 * New: Support `Dialog` as injection source.
 * Fix: Unwrap `InvocationTargetException` causes for more helpful exceptions.


Version 5.0.0 *(2014-04-21)*
----------------------------

 * New: `@InjectViews` annotation groups multiple IDs into a `List` or array.
 * New: `ButterKnife.apply` method applies an `Action`, `Setter`, or Android `Property` to views in
   a list.
 * New listeners!
   * `ViewPager`: `@OnPageChange`.
   * `AdapterView`: `@OnItemSelected`.
   * `TextView`: `@OnTextChanged`.
 * New: Multi-method listener support. Specify a `callback` argument to choose which method the
   binding is for. *(See `@OnItemSelected` for an example)*
 * Fix: Support for generic types which are declared with an upper-bound.
 * Fix: Use less sophisticated method injection inspection in the annotation processor. The previous
   method caused problems with some Eclipse configurations.


Version 4.0.1 *(2013-11-25)*
----------------------------

 * Fix: Correct a problem preventing the annotation processor to access Android types when certain
   `javac` configurations were used to build.


Version 4.0.0 *(2013-11-25)*
----------------------------

`Views` class is now named `ButterKnife`

 * New listeners!
   * `View`: `@OnLongClick` and `@OnFocusChanged`.
   * `TextView`: `@OnEditorAction`.
   * `AdapterView`: `@OnItemClick` and `@OnItemLongClick`.
   * `CompoundButton`: `@OnCheckedChanged`.
 * New: Views are now only checked to be `null` once if at least one of the fields and/or methods
   lack the `@Optional` annotation.
 * Fix: Do no emit redundant casts to `View` for methods.


Version 3.0.1 *(2013-11-12)*
----------------------------

 * Fix: Do not emit redundant casts to `View`.


Version 3.0.0 *(2013-09-10)*
----------------------------

 * New: Injections are now required. An exception will be thrown if a view is
   not found. Add `@Optional` annotation to suppress this verification.


Version 2.0.1 *(2013-07-18)*
----------------------------

 * New: Control debug logging via `Views.setDebug`.


Version 2.0.0 *(2013-07-16)*
----------------------------

 * New: `@OnClick` annotation for binding click listeners to methods!


Version 1.4.0 *(2013-06-03)*
----------------------------

 * New: `Views.reset` for settings injections back to `null` in a fragment's
   `onDestroyView` callback.
 * Fix: Support parent class injection when the parent class has generics.


Version 1.3.2 *(2013-04-27)*
----------------------------

 * Multiple injections of the same view ID only require a single find call.
 * Fix: Ensure injection happens on classes who do not have any injections but
   their superclasses do.


Version 1.3.1 *(2013-04-12)*
----------------------------

 * Fix: Parent class inflater resolution now generates correct code.


Version 1.3.0 *(2013-03-26)*
----------------------------

 * New: Injection on objects that have zero `@InjectView`-annotated fields will
   no longer throw an exception.


Version 1.2.2 *(2013-03-11)*
----------------------------

 * Fix: Prevent annotations on private classes.


Version 1.2.1 *(2013-03-11)*
----------------------------

 * Fix: Correct generated code for parent class inflation.
 * Fix: Allow injection on `protected`-scoped fields.


Version 1.2.0 *(2013-05-07)*
----------------------------

 * Support injection on any object using an Activity as the view root.
 * Support injection on views for their children.
 * Fix: Annotation errors now appear on the affected field in IDEs.


Version 1.1.1 *(2013-05-06)*
----------------------------

 * Fix: Verify that the target type extends from `View`.
 * Fix: Correct package name resolution in Eclipse 4.2


Version 1.1.0 *(2013-03-05)*
----------------------------

 * Perform injection on any object by passing a view root.
 * Fix: Correct naming of static inner-class injection points.
 * Fix: Enforce `findById` can only be used with child classes of `View`.


Version 1.0.0 *(2013-03-05)*
----------------------------

Initial release.
