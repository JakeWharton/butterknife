package butterknife.compiler;

import butterknife.BindAnim;
import butterknife.BindArray;
import butterknife.BindBitmap;
import butterknife.BindBool;
import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindDrawable;
import butterknife.BindFloat;
import butterknife.BindFont;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import butterknife.OnItemSelected;
import butterknife.OnLongClick;
import butterknife.OnPageChange;
import butterknife.OnTextChanged;
import butterknife.OnTouch;
import butterknife.Optional;
import butterknife.compiler.FieldTypefaceBinding.TypefaceStyles;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType;

import static butterknife.internal.Constants.NO_RES_ID;
import static java.util.Objects.requireNonNull;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

@AutoService(Processor.class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.DYNAMIC)
@SuppressWarnings("NullAway") // TODO fix all these...
public final class ButterKnifeProcessor extends AbstractProcessor {
  // TODO remove when http://b.android.com/187527 is released.
  private static final String OPTION_SDK_INT = "butterknife.minSdk";
  private static final String OPTION_DEBUGGABLE = "butterknife.debuggable";
  static final Id NO_ID = new Id(NO_RES_ID);
  static final String VIEW_TYPE = "android.view.View";
  static final String ACTIVITY_TYPE = "android.app.Activity";
  static final String DIALOG_TYPE = "android.app.Dialog";
  private static final String COLOR_STATE_LIST_TYPE = "android.content.res.ColorStateList";
  private static final String BITMAP_TYPE = "android.graphics.Bitmap";
  private static final String ANIMATION_TYPE = "android.view.animation.Animation";
  private static final String DRAWABLE_TYPE = "android.graphics.drawable.Drawable";
  private static final String TYPED_ARRAY_TYPE = "android.content.res.TypedArray";
  private static final String TYPEFACE_TYPE = "android.graphics.Typeface";
  private static final String NULLABLE_ANNOTATION_NAME = "Nullable";
  private static final String STRING_TYPE = "java.lang.String";
  private static final String LIST_TYPE = List.class.getCanonicalName();
  private static final List<Class<? extends Annotation>> LISTENERS = Arrays.asList(//
      OnCheckedChanged.class, //
      OnClick.class, //
      OnEditorAction.class, //
      OnFocusChange.class, //
      OnItemClick.class, //
      OnItemLongClick.class, //
      OnItemSelected.class, //
      OnLongClick.class, //
      OnPageChange.class, //
      OnTextChanged.class, //
      OnTouch.class //
  );

  private Types typeUtils;
  private Filer filer;
  private @Nullable Trees trees;

  private int sdk = 1;
  private boolean debuggable = true;

  private final RScanner rScanner = new RScanner();

  @Override public synchronized void init(ProcessingEnvironment env) {
    super.init(env);

    String sdk = env.getOptions().get(OPTION_SDK_INT);
    if (sdk != null) {
      try {
        this.sdk = Integer.parseInt(sdk);
      } catch (NumberFormatException e) {
        env.getMessager()
            .printMessage(Kind.WARNING, "Unable to parse supplied minSdk option '"
                + sdk
                + "'. Falling back to API 1 support.");
      }
    }

    debuggable = !"false".equals(env.getOptions().get(OPTION_DEBUGGABLE));

    typeUtils = env.getTypeUtils();
    filer = env.getFiler();
    try {
      trees = Trees.instance(processingEnv);
    } catch (IllegalArgumentException ignored) {
      try {
        // Get original ProcessingEnvironment from Gradle-wrapped one or KAPT-wrapped one.
        for (Field field : processingEnv.getClass().getDeclaredFields()) {
          if (field.getName().equals("delegate") || field.getName().equals("processingEnv")) {
            field.setAccessible(true);
            ProcessingEnvironment javacEnv = (ProcessingEnvironment) field.get(processingEnv);
            trees = Trees.instance(javacEnv);
            break;
          }
        }
      } catch (Throwable ignored2) {
      }
    }
  }

  @Override public Set<String> getSupportedOptions() {
    ImmutableSet.Builder<String> builder = ImmutableSet.builder();
    builder.add(OPTION_SDK_INT, OPTION_DEBUGGABLE);
    if (trees != null) {
      builder.add(IncrementalAnnotationProcessorType.ISOLATING.getProcessorOption());
    }
    return builder.build();
  }

  @Override public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new LinkedHashSet<>();
    for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
      types.add(annotation.getCanonicalName());
    }
    return types;
  }

  private Set<Class<? extends Annotation>> getSupportedAnnotations() {
    Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();

    annotations.add(BindAnim.class);
    annotations.add(BindArray.class);
    annotations.add(BindBitmap.class);
    annotations.add(BindBool.class);
    annotations.add(BindColor.class);
    annotations.add(BindDimen.class);
    annotations.add(BindDrawable.class);
    annotations.add(BindFloat.class);
    annotations.add(BindFont.class);
    annotations.add(BindInt.class);
    annotations.add(BindString.class);
    annotations.add(BindView.class);
    annotations.add(BindViews.class);
    annotations.addAll(LISTENERS);

    return annotations;
  }

  @Override public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
    Map<TypeElement, BindingSet> bindingMap = findAndParseTargets(env);

    for (Map.Entry<TypeElement, BindingSet> entry : bindingMap.entrySet()) {
      TypeElement typeElement = entry.getKey();
      BindingSet binding = entry.getValue();

      JavaFile javaFile = binding.brewJava(sdk, debuggable);
      try {
        javaFile.writeTo(filer);
      } catch (IOException e) {
        error(typeElement, "Unable to write binding for type %s: %s", typeElement, e.getMessage());
      }
    }

    return false;
  }

  private Map<TypeElement, BindingSet> findAndParseTargets(RoundEnvironment env) {
    Map<TypeElement, BindingSet.Builder> builderMap = new LinkedHashMap<>();
    Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();

    // Process each @BindAnim element.
    for (Element element : env.getElementsAnnotatedWith(BindAnim.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceAnimation(element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindAnim.class, e);
      }
    }

    // Process each @BindArray element.
    for (Element element : env.getElementsAnnotatedWith(BindArray.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceArray(element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindArray.class, e);
      }
    }

    // Process each @BindBitmap element.
    for (Element element : env.getElementsAnnotatedWith(BindBitmap.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceBitmap(element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindBitmap.class, e);
      }
    }

    // Process each @BindBool element.
    for (Element element : env.getElementsAnnotatedWith(BindBool.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceBool(element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindBool.class, e);
      }
    }

    // Process each @BindColor element.
    for (Element element : env.getElementsAnnotatedWith(BindColor.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceColor(element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindColor.class, e);
      }
    }

    // Process each @BindDimen element.
    for (Element element : env.getElementsAnnotatedWith(BindDimen.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceDimen(element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindDimen.class, e);
      }
    }

    // Process each @BindDrawable element.
    for (Element element : env.getElementsAnnotatedWith(BindDrawable.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceDrawable(element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindDrawable.class, e);
      }
    }

    // Process each @BindFloat element.
    for (Element element : env.getElementsAnnotatedWith(BindFloat.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceFloat(element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindFloat.class, e);
      }
    }

    // Process each @BindFont element.
    for (Element element : env.getElementsAnnotatedWith(BindFont.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceFont(element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindFont.class, e);
      }
    }

    // Process each @BindInt element.
    for (Element element : env.getElementsAnnotatedWith(BindInt.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceInt(element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindInt.class, e);
      }
    }

    // Process each @BindString element.
    for (Element element : env.getElementsAnnotatedWith(BindString.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceString(element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindString.class, e);
      }
    }

    // Process each @BindView element.
    for (Element element : env.getElementsAnnotatedWith(BindView.class)) {
      // we don't SuperficialValidation.validateElement(element)
      // so that an unresolved View type can be generated by later processing rounds
      try {
        parseBindView(element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindView.class, e);
      }
    }

    // Process each @BindViews element.
    for (Element element : env.getElementsAnnotatedWith(BindViews.class)) {
      // we don't SuperficialValidation.validateElement(element)
      // so that an unresolved View type can be generated by later processing rounds
      try {
        parseBindViews(element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindViews.class, e);
      }
    }

    // Process each annotation that corresponds to a listener.
    for (Class<? extends Annotation> listener : LISTENERS) {
      findAndParseListener(env, listener, builderMap, erasedTargetNames);
    }

    Map<TypeElement, ClasspathBindingSet> classpathBindings =
        findAllSupertypeBindings(builderMap, erasedTargetNames);

    // Associate superclass binders with their subclass binders. This is a queue-based tree walk
    // which starts at the roots (superclasses) and walks to the leafs (subclasses).
    Deque<Map.Entry<TypeElement, BindingSet.Builder>> entries =
        new ArrayDeque<>(builderMap.entrySet());
    Map<TypeElement, BindingSet> bindingMap = new LinkedHashMap<>();
    while (!entries.isEmpty()) {
      Map.Entry<TypeElement, BindingSet.Builder> entry = entries.removeFirst();

      TypeElement type = entry.getKey();
      BindingSet.Builder builder = entry.getValue();

      TypeElement parentType = findParentType(type, erasedTargetNames, classpathBindings.keySet());
      if (parentType == null) {
        bindingMap.put(type, builder.build());
      } else {
        BindingInformationProvider parentBinding = bindingMap.get(parentType);
        if (parentBinding == null) {
          parentBinding = classpathBindings.get(parentType);
        }
        if (parentBinding != null) {
          builder.setParent(parentBinding);
          bindingMap.put(type, builder.build());
        } else {
          // Has a superclass binding but we haven't built it yet. Re-enqueue for later.
          entries.addLast(entry);
        }
      }
    }

    return bindingMap;
  }

  private void logParsingError(Element element, Class<? extends Annotation> annotation,
      Exception e) {
    StringWriter stackTrace = new StringWriter();
    e.printStackTrace(new PrintWriter(stackTrace));
    error(element, "Unable to parse @%s binding.\n\n%s", annotation.getSimpleName(), stackTrace);
  }

  private boolean isInaccessibleViaGeneratedCode(Class<? extends Annotation> annotationClass,
      String targetThing, Element element) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify field or method modifiers.
    Set<Modifier> modifiers = element.getModifiers();
    if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
      error(element, "@%s %s must not be private or static. (%s.%s)",
          annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify containing type.
    if (enclosingElement.getKind() != CLASS) {
      error(enclosingElement, "@%s %s may only be contained in classes. (%s.%s)",
          annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify containing class visibility is not private.
    if (enclosingElement.getModifiers().contains(PRIVATE)) {
      error(enclosingElement, "@%s %s may not be contained in private classes. (%s.%s)",
          annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    return hasError;
  }

  private boolean isBindingInWrongPackage(Class<? extends Annotation> annotationClass,
      Element element) {
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
    String qualifiedName = enclosingElement.getQualifiedName().toString();

    if (qualifiedName.startsWith("android.")) {
      error(element, "@%s-annotated class incorrectly in Android framework package. (%s)",
          annotationClass.getSimpleName(), qualifiedName);
      return true;
    }
    if (qualifiedName.startsWith("java.")) {
      error(element, "@%s-annotated class incorrectly in Java framework package. (%s)",
          annotationClass.getSimpleName(), qualifiedName);
      return true;
    }

    return false;
  }

  private void parseBindView(Element element, Map<TypeElement, BindingSet.Builder> builderMap,
      Set<TypeElement> erasedTargetNames) {
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Start by verifying common generated code restrictions.
    boolean hasError = isInaccessibleViaGeneratedCode(BindView.class, "fields", element)
        || isBindingInWrongPackage(BindView.class, element);

    // Verify that the target type extends from View.
    TypeMirror elementType = element.asType();
    if (elementType.getKind() == TypeKind.TYPEVAR) {
      TypeVariable typeVariable = (TypeVariable) elementType;
      elementType = typeVariable.getUpperBound();
    }
    Name qualifiedName = enclosingElement.getQualifiedName();
    Name simpleName = element.getSimpleName();
    if (!isSubtypeOfType(elementType, VIEW_TYPE) && !isInterface(elementType)) {
      if (elementType.getKind() == TypeKind.ERROR) {
        note(element, "@%s field with unresolved type (%s) "
                + "must elsewhere be generated as a View or interface. (%s.%s)",
            BindView.class.getSimpleName(), elementType, qualifiedName, simpleName);
      } else {
        error(element, "@%s fields must extend from View or be an interface. (%s.%s)",
            BindView.class.getSimpleName(), qualifiedName, simpleName);
        hasError = true;
      }
    }

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    int id = element.getAnnotation(BindView.class).value();
    BindingSet.Builder builder = builderMap.get(enclosingElement);
    Id resourceId = elementToId(element, BindView.class, id);
    if (builder != null) {
      String existingBindingName = builder.findExistingBindingName(resourceId);
      if (existingBindingName != null) {
        error(element, "Attempt to use @%s for an already bound ID %d on '%s'. (%s.%s)",
            BindView.class.getSimpleName(), id, existingBindingName,
            enclosingElement.getQualifiedName(), element.getSimpleName());
        return;
      }
    } else {
      builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
    }

    String name = simpleName.toString();
    TypeName type = TypeName.get(elementType);
    boolean required = isFieldRequired(element);

    builder.addField(resourceId, new FieldViewBinding(name, type, required));

    // Add the type-erased version to the valid binding targets set.
    erasedTargetNames.add(enclosingElement);
  }

  private void parseBindViews(Element element, Map<TypeElement, BindingSet.Builder> builderMap,
      Set<TypeElement> erasedTargetNames) {
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Start by verifying common generated code restrictions.
    boolean hasError = isInaccessibleViaGeneratedCode(BindViews.class, "fields", element)
        || isBindingInWrongPackage(BindViews.class, element);

    // Verify that the type is a List or an array.
    TypeMirror elementType = element.asType();
    String erasedType = doubleErasure(elementType);
    TypeMirror viewType = null;
    FieldCollectionViewBinding.Kind kind = null;
    if (elementType.getKind() == TypeKind.ARRAY) {
      ArrayType arrayType = (ArrayType) elementType;
      viewType = arrayType.getComponentType();
      kind = FieldCollectionViewBinding.Kind.ARRAY;
    } else if (LIST_TYPE.equals(erasedType)) {
      DeclaredType declaredType = (DeclaredType) elementType;
      List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
      if (typeArguments.size() != 1) {
        error(element, "@%s List must have a generic component. (%s.%s)",
            BindViews.class.getSimpleName(), enclosingElement.getQualifiedName(),
            element.getSimpleName());
        hasError = true;
      } else {
        viewType = typeArguments.get(0);
      }
      kind = FieldCollectionViewBinding.Kind.LIST;
    } else {
      error(element, "@%s must be a List or array. (%s.%s)", BindViews.class.getSimpleName(),
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }
    if (viewType != null && viewType.getKind() == TypeKind.TYPEVAR) {
      TypeVariable typeVariable = (TypeVariable) viewType;
      viewType = typeVariable.getUpperBound();
    }

    // Verify that the target type extends from View.
    if (viewType != null && !isSubtypeOfType(viewType, VIEW_TYPE) && !isInterface(viewType)) {
      if (viewType.getKind() == TypeKind.ERROR) {
        note(element, "@%s List or array with unresolved type (%s) "
                + "must elsewhere be generated as a View or interface. (%s.%s)",
            BindViews.class.getSimpleName(), viewType, enclosingElement.getQualifiedName(),
            element.getSimpleName());
      } else {
        error(element, "@%s List or array type must extend from View or be an interface. (%s.%s)",
            BindViews.class.getSimpleName(), enclosingElement.getQualifiedName(),
            element.getSimpleName());
        hasError = true;
      }
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    int[] ids = element.getAnnotation(BindViews.class).value();
    if (ids.length == 0) {
      error(element, "@%s must specify at least one ID. (%s.%s)", BindViews.class.getSimpleName(),
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    Integer duplicateId = findDuplicate(ids);
    if (duplicateId != null) {
      error(element, "@%s annotation contains duplicate ID %d. (%s.%s)",
          BindViews.class.getSimpleName(), duplicateId, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    if (hasError) {
      return;
    }

    TypeName type = TypeName.get(requireNonNull(viewType));
    boolean required = isFieldRequired(element);

    BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
    builder.addFieldCollection(new FieldCollectionViewBinding(name, type, requireNonNull(kind),
        new ArrayList<>(elementToIds(element, BindViews.class, ids).values()), required));

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceAnimation(Element element,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is Animation.
    if (!ANIMATION_TYPE.equals(element.asType().toString())) {
      error(element, "@%s field type must be 'Animation'. (%s.%s)",
          BindAnim.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindAnim.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindAnim.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    int id = element.getAnnotation(BindAnim.class).value();
    Id resourceId = elementToId(element, BindAnim.class, id);

    BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
    builder.addResource(new FieldAnimationBinding(resourceId, name));

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceBool(Element element,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is bool.
    if (element.asType().getKind() != TypeKind.BOOLEAN) {
      error(element, "@%s field type must be 'boolean'. (%s.%s)",
          BindBool.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindBool.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindBool.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    int id = element.getAnnotation(BindBool.class).value();
    Id resourceId = elementToId(element, BindBool.class, id);
    BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
    builder.addResource(
        new FieldResourceBinding(resourceId, name, FieldResourceBinding.Type.BOOL));

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceColor(Element element,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is int or ColorStateList.
    boolean isColorStateList = false;
    TypeMirror elementType = element.asType();
    if (COLOR_STATE_LIST_TYPE.equals(elementType.toString())) {
      isColorStateList = true;
    } else if (elementType.getKind() != TypeKind.INT) {
      error(element, "@%s field type must be 'int' or 'ColorStateList'. (%s.%s)",
          BindColor.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindColor.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindColor.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    int id = element.getAnnotation(BindColor.class).value();
    Id resourceId = elementToId(element, BindColor.class, id);
    BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);

    FieldResourceBinding.Type colorStateList = FieldResourceBinding.Type.COLOR_STATE_LIST;
    FieldResourceBinding.Type color = FieldResourceBinding.Type.COLOR;
    builder.addResource(new FieldResourceBinding(
        resourceId,
        name,
        isColorStateList ? colorStateList : color));

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceDimen(Element element,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is int or ColorStateList.
    boolean isInt = false;
    TypeMirror elementType = element.asType();
    if (elementType.getKind() == TypeKind.INT) {
      isInt = true;
    } else if (elementType.getKind() != TypeKind.FLOAT) {
      error(element, "@%s field type must be 'int' or 'float'. (%s.%s)",
          BindDimen.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindDimen.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindDimen.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    int id = element.getAnnotation(BindDimen.class).value();
    Id resourceId = elementToId(element, BindDimen.class, id);
    BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
    builder.addResource(new FieldResourceBinding(resourceId, name,
        isInt ? FieldResourceBinding.Type.DIMEN_AS_INT : FieldResourceBinding.Type.DIMEN_AS_FLOAT));

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceBitmap(Element element,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is Bitmap.
    if (!BITMAP_TYPE.equals(element.asType().toString())) {
      error(element, "@%s field type must be 'Bitmap'. (%s.%s)",
          BindBitmap.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindBitmap.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindBitmap.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    int id = element.getAnnotation(BindBitmap.class).value();
    Id resourceId = elementToId(element, BindBitmap.class, id);
    BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
    builder.addResource(
        new FieldResourceBinding(resourceId, name, FieldResourceBinding.Type.BITMAP));

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceDrawable(Element element,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is Drawable.
    if (!DRAWABLE_TYPE.equals(element.asType().toString())) {
      error(element, "@%s field type must be 'Drawable'. (%s.%s)",
          BindDrawable.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindDrawable.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindDrawable.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    int id = element.getAnnotation(BindDrawable.class).value();
    int tint = element.getAnnotation(BindDrawable.class).tint();
    Map<Integer, Id> resourceIds = elementToIds(element, BindDrawable.class, new int[] {id, tint});

    BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
    builder.addResource(new FieldDrawableBinding(resourceIds.get(id), name, resourceIds.get(tint)));

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceFloat(Element element,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is float.
    if (element.asType().getKind() != TypeKind.FLOAT) {
      error(element, "@%s field type must be 'float'. (%s.%s)",
          BindFloat.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindFloat.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindFloat.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    int id = element.getAnnotation(BindFloat.class).value();
    Id resourceId = elementToId(element, BindFloat.class, id);
    BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
    builder.addResource(
        new FieldResourceBinding(resourceId, name, FieldResourceBinding.Type.FLOAT));

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceFont(Element element,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is a Typeface.
    if (!TYPEFACE_TYPE.equals(element.asType().toString())) {
      error(element, "@%s field type must be 'Typeface'. (%s.%s)",
          BindFont.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindFont.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindFont.class, element);

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    BindFont bindFont = element.getAnnotation(BindFont.class);

    int styleValue = bindFont.style();
    TypefaceStyles style = TypefaceStyles.fromValue(styleValue);
    if (style == null) {
      error(element, "@%s style must be NORMAL, BOLD, ITALIC, or BOLD_ITALIC. (%s.%s)",
          BindFont.class.getSimpleName(), enclosingElement.getQualifiedName(), name);
      hasError = true;
    }

    if (hasError) {
      return;
    }

    BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
    Id resourceId = elementToId(element, BindFont.class, bindFont.value());
    builder.addResource(new FieldTypefaceBinding(resourceId, name, style));

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceInt(Element element,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is int.
    if (element.asType().getKind() != TypeKind.INT) {
      error(element, "@%s field type must be 'int'. (%s.%s)", BindInt.class.getSimpleName(),
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindInt.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindInt.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    int id = element.getAnnotation(BindInt.class).value();
    Id resourceId = elementToId(element, BindInt.class, id);
    BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
    builder.addResource(
        new FieldResourceBinding(resourceId, name, FieldResourceBinding.Type.INT));

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceString(Element element,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is String.
    if (!STRING_TYPE.equals(element.asType().toString())) {
      error(element, "@%s field type must be 'String'. (%s.%s)",
          BindString.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindString.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindString.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    int id = element.getAnnotation(BindString.class).value();
    Id resourceId = elementToId(element, BindString.class, id);
    BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
    builder.addResource(
        new FieldResourceBinding(resourceId, name, FieldResourceBinding.Type.STRING));

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceArray(Element element,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is supported.
    FieldResourceBinding.Type type = getArrayResourceMethodName(element);
    if (type == null) {
      error(element,
          "@%s field type must be one of: String[], int[], CharSequence[], %s. (%s.%s)",
          BindArray.class.getSimpleName(), TYPED_ARRAY_TYPE, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindArray.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindArray.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    int id = element.getAnnotation(BindArray.class).value();
    Id resourceId = elementToId(element, BindArray.class, id);
    BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
    builder.addResource(new FieldResourceBinding(resourceId, name, requireNonNull(type)));

    erasedTargetNames.add(enclosingElement);
  }

  /**
   * Returns a method name from the {@code android.content.res.Resources} class for array resource
   * binding, null if the element type is not supported.
   */
  private static @Nullable FieldResourceBinding.Type getArrayResourceMethodName(Element element) {
    TypeMirror typeMirror = element.asType();
    if (TYPED_ARRAY_TYPE.equals(typeMirror.toString())) {
      return FieldResourceBinding.Type.TYPED_ARRAY;
    }
    if (TypeKind.ARRAY.equals(typeMirror.getKind())) {
      ArrayType arrayType = (ArrayType) typeMirror;
      String componentType = arrayType.getComponentType().toString();
      if (STRING_TYPE.equals(componentType)) {
        return FieldResourceBinding.Type.STRING_ARRAY;
      } else if ("int".equals(componentType)) {
        return FieldResourceBinding.Type.INT_ARRAY;
      } else if ("java.lang.CharSequence".equals(componentType)) {
        return FieldResourceBinding.Type.TEXT_ARRAY;
      }
    }
    return null;
  }

  /** Returns the first duplicate element inside an array, null if there are no duplicates. */
  private static @Nullable Integer findDuplicate(int[] array) {
    Set<Integer> seenElements = new LinkedHashSet<>();

    for (int element : array) {
      if (!seenElements.add(element)) {
        return element;
      }
    }

    return null;
  }

  /** Uses both {@link Types#erasure} and string manipulation to strip any generic types. */
  private String doubleErasure(TypeMirror elementType) {
    String name = typeUtils.erasure(elementType).toString();
    int typeParamStart = name.indexOf('<');
    if (typeParamStart != -1) {
      name = name.substring(0, typeParamStart);
    }
    return name;
  }

  private void findAndParseListener(RoundEnvironment env,
      Class<? extends Annotation> annotationClass,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
    for (Element element : env.getElementsAnnotatedWith(annotationClass)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseListenerAnnotation(annotationClass, element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));

        error(element, "Unable to generate view binder for @%s.\n\n%s",
            annotationClass.getSimpleName(), stackTrace.toString());
      }
    }
  }

  private void parseListenerAnnotation(Class<? extends Annotation> annotationClass, Element element,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames)
      throws Exception {
    // This should be guarded by the annotation's @Target but it's worth a check for safe casting.
    if (!(element instanceof ExecutableElement) || element.getKind() != METHOD) {
      throw new IllegalStateException(
          String.format("@%s annotation must be on a method.", annotationClass.getSimpleName()));
    }

    ExecutableElement executableElement = (ExecutableElement) element;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Assemble information on the method.
    Annotation annotation = element.getAnnotation(annotationClass);
    Method annotationValue = annotationClass.getDeclaredMethod("value");
    if (annotationValue.getReturnType() != int[].class) {
      throw new IllegalStateException(
          String.format("@%s annotation value() type not int[].", annotationClass));
    }

    int[] ids = (int[]) annotationValue.invoke(annotation);
    String name = executableElement.getSimpleName().toString();
    boolean required = isListenerRequired(executableElement);

    // Verify that the method and its containing class are accessible via generated code.
    boolean hasError = isInaccessibleViaGeneratedCode(annotationClass, "methods", element);
    hasError |= isBindingInWrongPackage(annotationClass, element);

    Integer duplicateId = findDuplicate(ids);
    if (duplicateId != null) {
      error(element, "@%s annotation for method contains duplicate ID %d. (%s.%s)",
          annotationClass.getSimpleName(), duplicateId, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    ListenerClass listener = annotationClass.getAnnotation(ListenerClass.class);
    if (listener == null) {
      throw new IllegalStateException(
          String.format("No @%s defined on @%s.", ListenerClass.class.getSimpleName(),
              annotationClass.getSimpleName()));
    }

    for (int id : ids) {
      if (id == NO_ID.value) {
        if (ids.length == 1) {
          if (!required) {
            error(element, "ID-free binding must not be annotated with @Optional. (%s.%s)",
                enclosingElement.getQualifiedName(), element.getSimpleName());
            hasError = true;
          }
        } else {
          error(element, "@%s annotation contains invalid ID %d. (%s.%s)",
              annotationClass.getSimpleName(), id, enclosingElement.getQualifiedName(),
              element.getSimpleName());
          hasError = true;
        }
      }
    }

    ListenerMethod method;
    ListenerMethod[] methods = listener.method();
    if (methods.length > 1) {
      throw new IllegalStateException(String.format("Multiple listener methods specified on @%s.",
          annotationClass.getSimpleName()));
    } else if (methods.length == 1) {
      if (listener.callbacks() != ListenerClass.NONE.class) {
        throw new IllegalStateException(
            String.format("Both method() and callback() defined on @%s.",
                annotationClass.getSimpleName()));
      }
      method = methods[0];
    } else {
      Method annotationCallback = annotationClass.getDeclaredMethod("callback");
      Enum<?> callback = (Enum<?>) annotationCallback.invoke(annotation);
      Field callbackField = callback.getDeclaringClass().getField(callback.name());
      method = callbackField.getAnnotation(ListenerMethod.class);
      if (method == null) {
        throw new IllegalStateException(
            String.format("No @%s defined on @%s's %s.%s.", ListenerMethod.class.getSimpleName(),
                annotationClass.getSimpleName(), callback.getDeclaringClass().getSimpleName(),
                callback.name()));
      }
    }

    // Verify that the method has equal to or less than the number of parameters as the listener.
    List<? extends VariableElement> methodParameters = executableElement.getParameters();
    if (methodParameters.size() > method.parameters().length) {
      error(element, "@%s methods can have at most %s parameter(s). (%s.%s)",
          annotationClass.getSimpleName(), method.parameters().length,
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    // Verify method return type matches the listener.
    TypeMirror returnType = executableElement.getReturnType();
    if (returnType instanceof TypeVariable) {
      TypeVariable typeVariable = (TypeVariable) returnType;
      returnType = typeVariable.getUpperBound();
    }
    String returnTypeString = returnType.toString();
    boolean hasReturnValue = !"void".equals(returnTypeString);
    if (!returnTypeString.equals(method.returnType()) && hasReturnValue) {
      error(element, "@%s methods must have a '%s' return type. (%s.%s)",
          annotationClass.getSimpleName(), method.returnType(),
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    if (hasError) {
      return;
    }

    Parameter[] parameters = Parameter.NONE;
    if (!methodParameters.isEmpty()) {
      parameters = new Parameter[methodParameters.size()];
      BitSet methodParameterUsed = new BitSet(methodParameters.size());
      String[] parameterTypes = method.parameters();
      for (int i = 0; i < methodParameters.size(); i++) {
        VariableElement methodParameter = methodParameters.get(i);
        TypeMirror methodParameterType = methodParameter.asType();
        if (methodParameterType instanceof TypeVariable) {
          TypeVariable typeVariable = (TypeVariable) methodParameterType;
          methodParameterType = typeVariable.getUpperBound();
        }

        for (int j = 0; j < parameterTypes.length; j++) {
          if (methodParameterUsed.get(j)) {
            continue;
          }
          if ((isSubtypeOfType(methodParameterType, parameterTypes[j])
              && isSubtypeOfType(methodParameterType, VIEW_TYPE))
              || isTypeEqual(methodParameterType, parameterTypes[j])
              || isInterface(methodParameterType)) {
            parameters[i] = new Parameter(j, TypeName.get(methodParameterType));
            methodParameterUsed.set(j);
            break;
          }
        }
        if (parameters[i] == null) {
          StringBuilder builder = new StringBuilder();
          builder.append("Unable to match @")
              .append(annotationClass.getSimpleName())
              .append(" method arguments. (")
              .append(enclosingElement.getQualifiedName())
              .append('.')
              .append(element.getSimpleName())
              .append(')');
          for (int j = 0; j < parameters.length; j++) {
            Parameter parameter = parameters[j];
            builder.append("\n\n  Parameter #")
                .append(j + 1)
                .append(": ")
                .append(methodParameters.get(j).asType().toString())
                .append("\n    ");
            if (parameter == null) {
              builder.append("did not match any listener parameters");
            } else {
              builder.append("matched listener parameter #")
                  .append(parameter.getListenerPosition() + 1)
                  .append(": ")
                  .append(parameter.getType());
            }
          }
          builder.append("\n\nMethods may have up to ")
              .append(method.parameters().length)
              .append(" parameter(s):\n");
          for (String parameterType : method.parameters()) {
            builder.append("\n  ").append(parameterType);
          }
          builder.append(
              "\n\nThese may be listed in any order but will be searched for from top to bottom.");
          error(executableElement, builder.toString());
          return;
        }
      }
    }

    MethodViewBinding binding =
        new MethodViewBinding(name, Arrays.asList(parameters), required, hasReturnValue);
    BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
    Map<Integer, Id> resourceIds = elementToIds(element, annotationClass, ids);

    for (Map.Entry<Integer, Id> entry : resourceIds.entrySet()) {
      if (!builder.addMethod(entry.getValue(), listener, method, binding)) {
        error(element, "Multiple listener methods with return value specified for ID %d. (%s.%s)",
            entry.getKey(), enclosingElement.getQualifiedName(), element.getSimpleName());
        return;
      }
    }

    // Add the type-erased version to the valid binding targets set.
    erasedTargetNames.add(enclosingElement);
  }

  private boolean isInterface(TypeMirror typeMirror) {
    return typeMirror instanceof DeclaredType
        && ((DeclaredType) typeMirror).asElement().getKind() == INTERFACE;
  }

  static boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
    if (isTypeEqual(typeMirror, otherType)) {
      return true;
    }
    if (typeMirror.getKind() != TypeKind.DECLARED) {
      return false;
    }
    DeclaredType declaredType = (DeclaredType) typeMirror;
    List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
    if (typeArguments.size() > 0) {
      StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
      typeString.append('<');
      for (int i = 0; i < typeArguments.size(); i++) {
        if (i > 0) {
          typeString.append(',');
        }
        typeString.append('?');
      }
      typeString.append('>');
      if (typeString.toString().equals(otherType)) {
        return true;
      }
    }
    Element element = declaredType.asElement();
    if (!(element instanceof TypeElement)) {
      return false;
    }
    TypeElement typeElement = (TypeElement) element;
    TypeMirror superType = typeElement.getSuperclass();
    if (isSubtypeOfType(superType, otherType)) {
      return true;
    }
    for (TypeMirror interfaceType : typeElement.getInterfaces()) {
      if (isSubtypeOfType(interfaceType, otherType)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isTypeEqual(TypeMirror typeMirror, String otherType) {
    return otherType.equals(typeMirror.toString());
  }

  private BindingSet.Builder getOrCreateBindingBuilder(
      Map<TypeElement, BindingSet.Builder> builderMap, TypeElement enclosingElement) {
    BindingSet.Builder builder = builderMap.get(enclosingElement);
    if (builder == null) {
      builder = BindingSet.newBuilder(enclosingElement);
      builderMap.put(enclosingElement, builder);
    }
    return builder;
  }

  /** Finds the parent binder type in the supplied sets, if any. */
  private @Nullable TypeElement findParentType(
      TypeElement typeElement, Set<TypeElement> parents, Set<TypeElement> classpathParents) {
    while (true) {
      typeElement = getSuperClass(typeElement);
      if (typeElement == null || parents.contains(typeElement)
          || classpathParents.contains(typeElement)) {
        return typeElement;
      }
    }
  }

  private Map<TypeElement, ClasspathBindingSet> findAllSupertypeBindings(
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> processedInThisRound) {
    Map<TypeElement, ClasspathBindingSet> classpathBindings = new HashMap<>();

    Set<Class<? extends Annotation>> supportedAnnotations = getSupportedAnnotations();
    Set<Class<? extends Annotation>> requireViewInConstructor =
        ImmutableSet.<Class<? extends Annotation>>builder()
            .addAll(LISTENERS).add(BindView.class).add(BindViews.class).build();
    supportedAnnotations.removeAll(requireViewInConstructor);

    for (TypeElement typeElement : builderMap.keySet()) {
      // Make sure to process superclass before subclass. This is because if there is a class that
      // requires a View in the constructor, all subclasses need it as well.
      Deque<TypeElement> superClasses = new ArrayDeque<>();
      TypeElement superClass = getSuperClass(typeElement);
      while (superClass != null && !processedInThisRound.contains(superClass)
          && !classpathBindings.containsKey(superClass)) {
        superClasses.addFirst(superClass);
        superClass = getSuperClass(superClass);
      }

      boolean parentHasConstructorWithView = false;
      while (!superClasses.isEmpty()) {
        TypeElement superclass = superClasses.removeFirst();
        ClasspathBindingSet classpathBinding =
            findBindingInfoForType(superclass, requireViewInConstructor, supportedAnnotations,
                parentHasConstructorWithView);
        if (classpathBinding != null) {
          parentHasConstructorWithView |= classpathBinding.constructorNeedsView();
          classpathBindings.put(superclass, classpathBinding);
        }
      }
    }
    return ImmutableMap.copyOf(classpathBindings);
  }

  private @Nullable ClasspathBindingSet findBindingInfoForType(
      TypeElement typeElement, Set<Class<? extends Annotation>> requireConstructorWithView,
      Set<Class<? extends Annotation>> otherAnnotations, boolean needsConstructorWithView) {
    boolean foundSupportedAnnotation = false;
    for (Element enclosedElement : typeElement.getEnclosedElements()) {
      for (Class<? extends Annotation> bindViewAnnotation : requireConstructorWithView) {
        if (enclosedElement.getAnnotation(bindViewAnnotation) != null) {
          return new ClasspathBindingSet(true, typeElement);
        }
      }
      for (Class<? extends Annotation> supportedAnnotation : otherAnnotations) {
        if (enclosedElement.getAnnotation(supportedAnnotation) != null) {
          if (needsConstructorWithView) {
            return new ClasspathBindingSet(true, typeElement);
          }
          foundSupportedAnnotation = true;
        }
      }
    }
    if (foundSupportedAnnotation) {
      return new ClasspathBindingSet(false, typeElement);
    } else {
      return null;
    }
  }

  private @Nullable TypeElement getSuperClass(TypeElement typeElement) {
    TypeMirror type = typeElement.getSuperclass();
    if (type.getKind() == TypeKind.NONE) {
      return null;
    }
    return (TypeElement) ((DeclaredType) type).asElement();
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  private void error(Element element, String message, Object... args) {
    printMessage(Kind.ERROR, element, message, args);
  }

  private void note(Element element, String message, Object... args) {
    printMessage(Kind.NOTE, element, message, args);
  }

  private void printMessage(Kind kind, Element element, String message, Object[] args) {
    if (args.length > 0) {
      message = String.format(message, args);
    }

    processingEnv.getMessager().printMessage(kind, message, element);
  }

  private Id elementToId(Element element, Class<? extends Annotation> annotation, int value) {
    JCTree tree = (JCTree) trees.getTree(element, getMirror(element, annotation));
    if (tree != null) { // tree can be null if the references are compiled types and not source
      rScanner.reset();
      tree.accept(rScanner);
      if (!rScanner.resourceIds.isEmpty()) {
        return rScanner.resourceIds.values().iterator().next();
      }
    }
    return new Id(value);
  }

  private Map<Integer, Id> elementToIds(Element element, Class<? extends Annotation> annotation,
      int[] values) {
    Map<Integer, Id> resourceIds = new LinkedHashMap<>();
    JCTree tree = (JCTree) trees.getTree(element, getMirror(element, annotation));
    if (tree != null) { // tree can be null if the references are compiled types and not source
      rScanner.reset();
      tree.accept(rScanner);
      resourceIds = rScanner.resourceIds;
    }

    // Every value looked up should have an Id
    for (int value : values) {
      resourceIds.putIfAbsent(value, new Id(value));
    }
    return resourceIds;
  }

  private static boolean hasAnnotationWithName(Element element, String simpleName) {
    for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
      String annotationName = mirror.getAnnotationType().asElement().getSimpleName().toString();
      if (simpleName.equals(annotationName)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isFieldRequired(Element element) {
    return !hasAnnotationWithName(element, NULLABLE_ANNOTATION_NAME);
  }

  private static boolean isListenerRequired(ExecutableElement element) {
    return element.getAnnotation(Optional.class) == null;
  }

  private static @Nullable AnnotationMirror getMirror(Element element,
      Class<? extends Annotation> annotation) {
    for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
      if (annotationMirror.getAnnotationType().toString().equals(annotation.getCanonicalName())) {
        return annotationMirror;
      }
    }
    return null;
  }

  private static class RScanner extends TreeScanner {
    Map<Integer, Id> resourceIds = new LinkedHashMap<>();

    @Override public void visitSelect(JCTree.JCFieldAccess jcFieldAccess) {
      Symbol symbol = jcFieldAccess.sym;
      if (symbol.getEnclosingElement() != null
          && symbol.getEnclosingElement().getEnclosingElement() != null
          && symbol.getEnclosingElement().getEnclosingElement().enclClass() != null) {
        try {
          int value = (Integer) requireNonNull(((Symbol.VarSymbol) symbol).getConstantValue());
          resourceIds.put(value, new Id(value, symbol));
        } catch (Exception ignored) { }
      }
    }

    @Override public void visitLiteral(JCTree.JCLiteral jcLiteral) {
      try {
        int value = (Integer) jcLiteral.value;
        resourceIds.put(value, new Id(value));
      } catch (Exception ignored) { }
    }

    void reset() {
      resourceIds.clear();
    }
  }
}
