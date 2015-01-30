Change Log
==========

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
