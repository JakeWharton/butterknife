package butterknife.compiler;

import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static butterknife.compiler.ButterKnifeProcessor.VIEW_TYPE;
import static java.util.Collections.singletonList;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

final class BindingClass {
  private static final ClassName FINDER = ClassName.get("butterknife.internal", "Finder");
  private static final ClassName VIEW_BINDER = ClassName.get("butterknife.internal", "ViewBinder");
  private static final ClassName UTILS = ClassName.get("butterknife.internal", "Utils");
  private static final ClassName VIEW = ClassName.get("android.view", "View");
  private static final ClassName CONTEXT = ClassName.get("android.content", "Context");
  private static final ClassName RESOURCES = ClassName.get("android.content.res", "Resources");
  private static final ClassName THEME = RESOURCES.nestedClass("Theme");
  private static final ClassName UNBINDER = ClassName.get("butterknife", "Unbinder");
  private static final ClassName BITMAP_FACTORY =
      ClassName.get("android.graphics", "BitmapFactory");
  private static final String BIND_TO_TARGET = "bindToTarget";

  private final Map<Id, ViewBindings> viewIdMap = new LinkedHashMap<>();
  private final Map<FieldCollectionViewBinding, List<Id>> collectionBindings =
      new LinkedHashMap<>();
  private final List<FieldBitmapBinding> bitmapBindings = new ArrayList<>();
  private final List<FieldDrawableBinding> drawableBindings = new ArrayList<>();
  private final List<FieldResourceBinding> resourceBindings = new ArrayList<>();
  private final boolean isFinal;
  private final TypeName targetTypeName;
  private final ClassName binderClassName;
  private final ClassName unbinderClassName;
  private BindingClass parentBinding;

  BindingClass(TypeName targetTypeName, ClassName binderClassName, ClassName unbinderClassName,
      boolean isFinal) {
    this.isFinal = isFinal;
    this.targetTypeName = targetTypeName;
    this.binderClassName = binderClassName;
    this.unbinderClassName = unbinderClassName;
  }

  void addBitmap(FieldBitmapBinding binding) {
    bitmapBindings.add(binding);
  }

  void addDrawable(FieldDrawableBinding binding) {
    drawableBindings.add(binding);
  }

  void addField(Id id, FieldViewBinding binding) {
    getOrCreateViewBindings(id).setFieldBinding(binding);
  }

  void addFieldCollection(List<Id> ids, FieldCollectionViewBinding binding) {
    collectionBindings.put(binding, ids);
  }

  boolean addMethod(
      Id id,
      ListenerClass listener,
      ListenerMethod method,
      MethodViewBinding binding) {
    ViewBindings viewBindings = getOrCreateViewBindings(id);
    if (viewBindings.hasMethodBinding(listener, method) && !"void".equals(method.returnType())) {
      return false;
    }
    viewBindings.addMethodBinding(listener, method, binding);
    return true;
  }

  void addResource(FieldResourceBinding binding) {
    resourceBindings.add(binding);
  }

  void setParent(BindingClass parent) {
    this.parentBinding = parent;
  }

  ViewBindings getViewBinding(Id id) {
    return viewIdMap.get(id);
  }

  private ViewBindings getOrCreateViewBindings(Id id) {
    ViewBindings viewId = viewIdMap.get(id);
    if (viewId == null) {
      viewId = new ViewBindings(id);
      viewIdMap.put(id, viewId);
    }
    return viewId;
  }

  Collection<JavaFile> brewJava() {
    TypeSpec.Builder result = TypeSpec.classBuilder(binderClassName)
        .addModifiers(PUBLIC, FINAL)
        .addSuperinterface(ParameterizedTypeName.get(VIEW_BINDER, targetTypeName));

    result.addMethod(createBindMethod(targetTypeName));

    List<JavaFile> files = new ArrayList<>();
    if (isGeneratingUnbinder()) {
      files.add(JavaFile.builder(unbinderClassName.packageName(), createUnbinderClass())
          .addFileComment("Generated code from Butter Knife. Do not modify!")
          .build()
      );
    } else if (!isFinal) {
      result.addMethod(createBindToTargetMethod());
    }

    files.add(JavaFile.builder(binderClassName.packageName(), result.build())
        .addFileComment("Generated code from Butter Knife. Do not modify!")
        .build());

    return files;
  }

  private TypeSpec createUnbinderClass() {
    TypeSpec.Builder result = TypeSpec.classBuilder(unbinderClassName.simpleName())
        .addModifiers(PUBLIC);

    TypeName targetType;
    if (isFinal) {
      result.addModifiers(FINAL);
      targetType = targetTypeName;
    } else {
      targetType = TypeVariableName.get("T");
      result.addTypeVariable(TypeVariableName.get("T", targetTypeName));
    }

    if (hasInheritedUnbinder()) {
      result.superclass(ParameterizedTypeName.get(getInheritedUnbinder(), targetType));
    } else {
      result.addSuperinterface(UNBINDER);
      result.addField(targetType, "target", isFinal ? PRIVATE : PROTECTED);
    }

    result.addMethod(createUnbinderConstructor(targetType));
    if (hasViewBindings()) {
      result.addMethod(createUnbindInterfaceMethod(result, targetType));
    }

    return result.build();
  }

  private MethodSpec createUnbinderConstructor(TypeName targetType) {
    MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
        .addModifiers(PUBLIC);

    if (hasMethodBindings()) {
      constructor.addParameter(targetType, "target", FINAL);
    } else {
      constructor.addParameter(targetType, "target");
    }

    if (bindNeedsFinder()) {
      if (methodBindingsNeedFinder()) {
        constructor.addParameter(FINDER, "finder", FINAL);
      } else {
        constructor.addParameter(FINDER, "finder");
      }
      constructor.addParameter(Object.class, "source");
    }
    if (bindNeedsResources()) {
      constructor.addParameter(RESOURCES, "res");
    }
    if (bindNeedsTheme()) {
      constructor.addParameter(THEME, "theme");
    }

    if (hasInheritedUnbinder()) {
      CodeBlock.Builder invoke = CodeBlock.builder();
      invoke.add("super(target");
      if (parentBinding.bindNeedsFinder()) invoke.add(", finder, source");
      if (parentBinding.bindNeedsResources()) invoke.add(", res");
      if (parentBinding.bindNeedsTheme()) invoke.add(", theme");
      constructor.addStatement("$L", invoke.add(")").build());
    } else {
      constructor.addStatement("this.target = target");
    }
    constructor.addCode("\n");

    generateBindViewBody(constructor);

    return constructor.build();
  }

  private MethodSpec createUnbindInterfaceMethod(TypeSpec.Builder unbinderClass,
      TypeName targetType) {
    MethodSpec.Builder result = MethodSpec.methodBuilder("unbind")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC);
    boolean rootUnbinderWithFields = !hasInheritedUnbinder() && hasFieldBindings();
    if (hasFieldBindings() || rootUnbinderWithFields) {
      result.addStatement("$T target = this.target", targetType);
    }
    if (!hasInheritedUnbinder()) {
      String target = rootUnbinderWithFields ? "target" : "this.target";
      result.addStatement("if ($N == null) throw new $T($S)", target, IllegalStateException.class,
          "Bindings already cleared.");
    } else {
      result.addStatement("super.unbind()");
    }

    if (hasFieldBindings()) {
      result.addCode("\n");
      for (ViewBindings bindings : viewIdMap.values()) {
        if (bindings.getFieldBinding() != null) {
          result.addStatement("target.$L = null", bindings.getFieldBinding().getName());
        }
      }
      for (FieldCollectionViewBinding fieldCollectionBinding : collectionBindings.keySet()) {
        result.addStatement("target.$L = null", fieldCollectionBinding.getName());
      }
    }

    if (hasMethodBindings()) {
      result.addCode("\n");
      for (ViewBindings bindings : viewIdMap.values()) {
        addFieldAndUnbindStatement(unbinderClass, result, bindings);
      }
    }

    if (!hasInheritedUnbinder()) {
      result.addCode("\n");
      result.addStatement("this.target = null");
    }

    return result.build();
  }

  private void addFieldAndUnbindStatement(TypeSpec.Builder result, MethodSpec.Builder unbindMethod,
      ViewBindings bindings) {
    // Only add fields to the unbinder if there are method bindings.
    Map<ListenerClass, Map<ListenerMethod, Set<MethodViewBinding>>> classMethodBindings =
        bindings.getMethodBindings();
    if (classMethodBindings.isEmpty()) {
      return;
    }

    String fieldName = "target";
    if (!bindings.isBoundToRoot()) {
      fieldName = "view" + bindings.getId().value;
      result.addField(VIEW, fieldName, PRIVATE);
    }

    // We only need to emit the null check if there are zero required bindings.
    boolean needsNullChecked = bindings.getRequiredBindings().isEmpty();
    if (needsNullChecked) {
      unbindMethod.beginControlFlow("if ($N != null)", fieldName);
    }

    for (ListenerClass listenerClass : classMethodBindings.keySet()) {
      // We need to keep a reference to the listener
      // in case we need to unbind it via a remove method.
      boolean requiresRemoval = !"".equals(listenerClass.remover());
      String listenerField = "null";
      if (requiresRemoval) {
        TypeName listenerClassName = bestGuess(listenerClass.type());
        listenerField = fieldName + ((ClassName) listenerClassName).simpleName();
        result.addField(listenerClassName, listenerField, PRIVATE);
      }

      if (!VIEW_TYPE.equals(listenerClass.targetType())) {
        unbindMethod.addStatement("(($T) $N).$N($N)", bestGuess(listenerClass.targetType()),
            fieldName, removerOrSetter(listenerClass, requiresRemoval), listenerField);
      } else {
        unbindMethod.addStatement("$N.$N($N)", fieldName,
            removerOrSetter(listenerClass, requiresRemoval), listenerField);
      }

      if (requiresRemoval) {
        unbindMethod.addStatement("$N = null", listenerField);
      }
    }

    if (!bindings.isBoundToRoot()) {
      unbindMethod.addStatement("$N = null", fieldName);
    }

    if (needsNullChecked) {
      unbindMethod.endControlFlow();
    }
  }

  private String removerOrSetter(ListenerClass listenerClass, boolean requiresRemoval) {
    return requiresRemoval
        ? listenerClass.remover()
        : listenerClass.setter();
  }

  private MethodSpec createBindMethod(TypeName targetType) {
    MethodSpec.Builder result = MethodSpec.methodBuilder("bind")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(UNBINDER)
        .addParameter(FINDER, "finder");
    if (isFinal && hasMethodBindings()) {
      result.addParameter(targetType, "target", FINAL);
    } else {
      result.addParameter(targetType, "target");
    }
    result.addParameter(Object.class, "source");

    boolean needsFinder = bindNeedsFinder();
    boolean needsResources = bindNeedsResources();
    boolean needsTheme = bindNeedsTheme();

    if (needsResources) {
      if (needsTheme) {
        result.addStatement("$T context = finder.getContext(source)", CONTEXT);
        result.addStatement("$T res = context.getResources()", RESOURCES);
        result.addStatement("$T theme = context.getTheme()", THEME);
      } else {
        result.addStatement("$T res = finder.getContext(source).getResources()", RESOURCES);
      }
    }

    if (isFinal && !isGeneratingUnbinder()) {
      if (needsResources) {
        result.addCode("\n");
      }
      generateBindViewBody(result);
      result.addCode("\n");
    }

    CodeBlock.Builder invoke = CodeBlock.builder();
    if (isGeneratingUnbinder()) {
      if (isFinal) {
        invoke.add("return new $T", unbinderClassName);
      } else {
        invoke.add("return new $T<>", unbinderClassName);
      }
    } else if (!isFinal) {
      invoke.add("$N", BIND_TO_TARGET);
    }
    if (isGeneratingUnbinder() || !isFinal) {
      invoke.add("(target");
      if (needsFinder) invoke.add(", finder, source");
      if (needsResources) invoke.add(", res");
      if (needsTheme) invoke.add(", theme");
      result.addStatement("$L", invoke.add(")").build());
    }

    if (!isGeneratingUnbinder()) {
      result.addStatement("return $T.EMPTY", UNBINDER);
    }

    return result.build();
  }

  private MethodSpec createBindToTargetMethod() {
    MethodSpec.Builder result = MethodSpec.methodBuilder(BIND_TO_TARGET)
        .addModifiers(PUBLIC, STATIC);

    if (hasMethodBindings()) {
      result.addParameter(targetTypeName, "target", FINAL);
    } else {
      result.addParameter(targetTypeName, "target");
    }

    if (bindNeedsResources()) {
      result.addParameter(RESOURCES, "res");
    }
    if (bindNeedsTheme()) {
      result.addParameter(THEME, "theme");
    }

    generateBindViewBody(result);

    return result.build();
  }

  private void generateBindViewBody(MethodSpec.Builder result) {
    if (hasUnqualifiedResourceBindings()) {
      // Aapt can change IDs out from underneath us, just suppress since all will work at runtime.
      result.addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
          .addMember("value", "$S", "ResourceType")
          .build());
    }

    if (!hasInheritedUnbinder() && hasParentBinding()) {
      CodeBlock.Builder invoke = CodeBlock.builder() //
          .add("$T.$N(target", parentBinding.binderClassName, BIND_TO_TARGET);
      if (parentBinding.bindNeedsFinder()) invoke.add(", finder, source");
      if (parentBinding.bindNeedsResources()) invoke.add(", res");
      if (parentBinding.bindNeedsTheme()) invoke.add(", theme");
      result.addStatement("$L", invoke.add(")").build());
      result.addCode("\n");
    }

    if (hasViewBindings()) {
      if (bindNeedsViewLocal()) {
        // Local variable in which all views will be temporarily stored.
        result.addStatement("$T view", VIEW);
      }

      // Loop over each view bindings and emit it.
      for (ViewBindings bindings : viewIdMap.values()) {
        addViewBindings(result, bindings);
      }

      // Loop over each collection binding and emit it.
      for (Map.Entry<FieldCollectionViewBinding, List<Id>> entry : collectionBindings.entrySet()) {
        emitCollectionBinding(result, entry.getKey(), entry.getValue());
      }

      if (hasResourceBindings()) {
        result.addCode("\n");
      }
    }

    if (hasResourceBindings()) {
      for (FieldBitmapBinding binding : bitmapBindings) {
        result.addStatement("target.$L = $T.decodeResource(res, $L)", binding.getName(),
            BITMAP_FACTORY, binding.getId().code);
      }

      for (FieldDrawableBinding binding : drawableBindings) {
        Id tintAttributeId = binding.getTintAttributeId();
        if (tintAttributeId.value != 0) {
          result.addStatement("target.$L = $T.getTintedDrawable(res, theme, $L, $L)",
              binding.getName(), UTILS, binding.getId().code, tintAttributeId.code);
        } else {
          result.addStatement("target.$L = $T.getDrawable(res, theme, $L)", binding.getName(),
              UTILS, binding.getId().code);
        }
      }

      for (FieldResourceBinding binding : resourceBindings) {
        // TODO being themeable is poor correlation to the need to use Utils.
        if (binding.isThemeable()) {
          result.addStatement("target.$L = $T.$L(res, theme, $L)", binding.getName(),
              UTILS, binding.getMethod(), binding.getId().code);
        } else {
          result.addStatement("target.$L = res.$L($L)", binding.getName(), binding.getMethod(),
              binding.getId().code);
        }
      }
    }
  }

  private void emitCollectionBinding(
      MethodSpec.Builder result,
      FieldCollectionViewBinding binding,
      List<Id> ids) {
    String ofName;
    switch (binding.getKind()) {
      case ARRAY:
        ofName = "arrayOf";
        break;
      case LIST:
        ofName = "listOf";
        break;
      default:
        throw new IllegalStateException("Unknown kind: " + binding.getKind());
    }

    CodeBlock.Builder builder = CodeBlock.builder();
    for (int i = 0; i < ids.size(); i++) {
      if (i > 0) {
        builder.add(", ");
      }
      builder.add("\n");
      if (requiresCast(binding.getType())) {
        builder.add("($T) ", binding.getType());
      }
      if (binding.isRequired()) {
        builder.add("finder.findRequiredView(source, $L, $S)", ids.get(i).code,
            asHumanDescription(singletonList(binding)));
      } else {
        builder.add("finder.findOptionalView(source, $L)", ids.get(i).code);
      }
    }

    result.addStatement("target.$L = $T.$L($L)", binding.getName(), UTILS, ofName, builder.build());
  }

  private void addViewBindings(MethodSpec.Builder result, ViewBindings bindings) {
    if (bindings.isSingleFieldBinding()) {
      // Optimize the common case where there's a single binding directly to a field.
      FieldViewBinding fieldBinding = bindings.getFieldBinding();
      CodeBlock.Builder invoke = CodeBlock.builder()
          .add("target.$L = finder.find", fieldBinding.getName());
      invoke.add(fieldBinding.isRequired() ? "RequiredView" : "OptionalView");
      if (requiresCast(fieldBinding.getType())) {
        invoke.add("AsType");
      }
      invoke.add("(source, $L", bindings.getId().code);
      if (fieldBinding.isRequired() || requiresCast(fieldBinding.getType())) {
        invoke.add(", $S", asHumanDescription(singletonList(fieldBinding)));
      }
      if (requiresCast(fieldBinding.getType())) {
        invoke.add(", $T.class", fieldBinding.getRawType());
      }
      result.addStatement("$L)", invoke.build());
      return;
    }

    List<ViewBinding> requiredViewBindings = bindings.getRequiredBindings();
    if (requiredViewBindings.isEmpty()) {
      result.addStatement("view = finder.findOptionalView(source, $L)", bindings.getId().code);
    } else if (!bindings.isBoundToRoot()) {
      result.addStatement("view = finder.findRequiredView(source, $L, $S)", bindings.getId().code,
          asHumanDescription(requiredViewBindings));
    }

    addFieldBindings(result, bindings);
    addMethodBindings(result, bindings);
  }

  private void addFieldBindings(MethodSpec.Builder result, ViewBindings bindings) {
    FieldViewBinding fieldBinding = bindings.getFieldBinding();
    if (fieldBinding != null) {
      if (requiresCast(fieldBinding.getType())) {
        result.addStatement("target.$L = finder.castView(view, $L, $S)", fieldBinding.getName(),
            bindings.getId().code, asHumanDescription(singletonList(fieldBinding)));
      } else {
        result.addStatement("target.$L = view", fieldBinding.getName());
      }
    }
  }

  private void addMethodBindings(MethodSpec.Builder result, ViewBindings bindings) {
    Map<ListenerClass, Map<ListenerMethod, Set<MethodViewBinding>>> classMethodBindings =
        bindings.getMethodBindings();
    if (classMethodBindings.isEmpty()) {
      return;
    }

    // We only need to emit the null check if there are zero required bindings.
    boolean needsNullChecked = bindings.getRequiredBindings().isEmpty();
    if (needsNullChecked) {
      result.beginControlFlow("if (view != null)");
    }

    // Add the view reference to the unbinder.
    String fieldName = "target";
    String bindName = "target";
    if (!bindings.isBoundToRoot()) {
      fieldName = "view" + bindings.getId().value;
      bindName = "view";

      if (isGeneratingUnbinder()) {
        result.addStatement("$L = view", fieldName);
      }
    }

    for (Map.Entry<ListenerClass, Map<ListenerMethod, Set<MethodViewBinding>>> e
        : classMethodBindings.entrySet()) {
      ListenerClass listener = e.getKey();
      Map<ListenerMethod, Set<MethodViewBinding>> methodBindings = e.getValue();

      TypeSpec.Builder callback = TypeSpec.anonymousClassBuilder("")
          .superclass(ClassName.bestGuess(listener.type()));

      for (ListenerMethod method : getListenerMethods(listener)) {
        MethodSpec.Builder callbackMethod = MethodSpec.methodBuilder(method.name())
            .addAnnotation(Override.class)
            .addModifiers(PUBLIC)
            .returns(bestGuess(method.returnType()));
        String[] parameterTypes = method.parameters();
        for (int i = 0, count = parameterTypes.length; i < count; i++) {
          callbackMethod.addParameter(bestGuess(parameterTypes[i]), "p" + i);
        }

        boolean hasReturnType = !"void".equals(method.returnType());
        CodeBlock.Builder builder = CodeBlock.builder();
        if (hasReturnType) {
          builder.add("return ");
        }

        if (methodBindings.containsKey(method)) {
          for (MethodViewBinding binding : methodBindings.get(method)) {
            builder.add("target.$L(", binding.getName());
            List<Parameter> parameters = binding.getParameters();
            String[] listenerParameters = method.parameters();
            for (int i = 0, count = parameters.size(); i < count; i++) {
              if (i > 0) {
                builder.add(", ");
              }

              Parameter parameter = parameters.get(i);
              int listenerPosition = parameter.getListenerPosition();

              if (parameter.requiresCast(listenerParameters[listenerPosition])) {
                builder.add("finder.<$T>castParam(p$L, $S, $L, $S, $L)", parameter.getType(),
                    listenerPosition, method.name(), listenerPosition, binding.getName(), i);
              } else {
                builder.add("p$L", listenerPosition);
              }
            }
            builder.add(");\n");
          }
        } else if (hasReturnType) {
          builder.add("$L;\n", method.defaultReturn());
        }
        callbackMethod.addCode(builder.build());
        callback.addMethod(callbackMethod.build());
      }

      boolean requiresRemoval = isGeneratingUnbinder() && listener.remover().length() != 0;
      String listenerField = null;
      if (requiresRemoval) {
        TypeName listenerClassName = bestGuess(listener.type());
        listenerField = fieldName + ((ClassName) listenerClassName).simpleName();
        result.addStatement("this.$L = $L", listenerField, callback.build());
      }

      if (!VIEW_TYPE.equals(listener.targetType())) {
        result.addStatement("(($T) $N).$L($L)", bestGuess(listener.targetType()), bindName,
            listener.setter(), requiresRemoval ? listenerField : callback.build());
      } else {
        result.addStatement("$N.$L($L)", bindName, listener.setter(),
            requiresRemoval ? listenerField : callback.build());
      }
    }

    if (needsNullChecked) {
      result.endControlFlow();
    }
  }

  static List<ListenerMethod> getListenerMethods(ListenerClass listener) {
    if (listener.method().length == 1) {
      return Arrays.asList(listener.method());
    }

    try {
      List<ListenerMethod> methods = new ArrayList<>();
      Class<? extends Enum<?>> callbacks = listener.callbacks();
      for (Enum<?> callbackMethod : callbacks.getEnumConstants()) {
        Field callbackField = callbacks.getField(callbackMethod.name());
        ListenerMethod method = callbackField.getAnnotation(ListenerMethod.class);
        if (method == null) {
          throw new IllegalStateException(String.format("@%s's %s.%s missing @%s annotation.",
              callbacks.getEnclosingClass().getSimpleName(), callbacks.getSimpleName(),
              callbackMethod.name(), ListenerMethod.class.getSimpleName()));
        }
        methods.add(method);
      }
      return methods;
    } catch (NoSuchFieldException e) {
      throw new AssertionError(e);
    }
  }

  static String asHumanDescription(Collection<? extends ViewBinding> bindings) {
    Iterator<? extends ViewBinding> iterator = bindings.iterator();
    switch (bindings.size()) {
      case 1:
        return iterator.next().getDescription();
      case 2:
        return iterator.next().getDescription() + " and " + iterator.next().getDescription();
      default:
        StringBuilder builder = new StringBuilder();
        for (int i = 0, count = bindings.size(); i < count; i++) {
          if (i != 0) {
            builder.append(", ");
          }
          if (i == count - 1) {
            builder.append("and ");
          }
          builder.append(iterator.next().getDescription());
        }
        return builder.toString();
    }
  }

  static TypeName bestGuess(String type) {
    switch (type) {
      case "void": return TypeName.VOID;
      case "boolean": return TypeName.BOOLEAN;
      case "byte": return TypeName.BYTE;
      case "char": return TypeName.CHAR;
      case "double": return TypeName.DOUBLE;
      case "float": return TypeName.FLOAT;
      case "int": return TypeName.INT;
      case "long": return TypeName.LONG;
      case "short": return TypeName.SHORT;
      default:
        int left = type.indexOf('<');
        if (left != -1) {
          ClassName typeClassName = ClassName.bestGuess(type.substring(0, left));
          List<TypeName> typeArguments = new ArrayList<>();
          do {
            typeArguments.add(WildcardTypeName.subtypeOf(Object.class));
            left = type.indexOf('<', left + 1);
          } while (left != -1);
          return ParameterizedTypeName.get(typeClassName,
              typeArguments.toArray(new TypeName[typeArguments.size()]));
        }
        return ClassName.bestGuess(type);
    }
  }

  /** True when this type has a parent view binder type. */
  private boolean hasParentBinding() {
    return parentBinding != null;
  }

  /** True when this type contains an unbinder subclass. */
  private boolean isGeneratingUnbinder() {
    return hasViewBindings() || hasInheritedUnbinder();
  }

  /** True when any of this type's parents contain an unbinder subclass. */
  private boolean hasInheritedUnbinder() {
    return hasParentBinding() && parentBinding.isGeneratingUnbinder();
  }

  /** Return the nearest unbinder subclass from this type's parents. */
  private ClassName getInheritedUnbinder() {
    return parentBinding.unbinderClassName;
  }

  /** True when this type's bindings require a view hierarchy. */
  private boolean hasViewBindings() {
    return !viewIdMap.isEmpty() || !collectionBindings.isEmpty();
  }

  /** True when this type's bindings require Android's {@code Resources}. */
  private boolean hasResourceBindings() {
    return !(bitmapBindings.isEmpty() && drawableBindings.isEmpty() && resourceBindings.isEmpty());
  }

  /** True when this type's bindings use raw integer values instead of {@code R} references. */
  private boolean hasUnqualifiedResourceBindings() {
    for (FieldBitmapBinding binding : bitmapBindings) {
      if (!binding.getId().qualifed) {
        return true;
      }
    }
    for (FieldDrawableBinding binding : drawableBindings) {
      if (!binding.getId().qualifed) {
        return true;
      }
    }
    for (FieldResourceBinding binding : resourceBindings) {
      if (!binding.getId().qualifed) {
        return true;
      }
    }
    return false;
  }

  /** True when this type's resource bindings require Android's {@code Theme}. */
  private boolean hasResourceBindingsNeedingTheme() {
    if (!drawableBindings.isEmpty()) {
      return true;
    }
    for (FieldResourceBinding resourceBinding : resourceBindings) {
      if (resourceBinding.isThemeable()) {
        return true;
      }
    }
    return false;
  }

  private boolean hasMethodBindings() {
    for (ViewBindings viewBindings : viewIdMap.values()) {
      if (!viewBindings.getMethodBindings().isEmpty()) {
        return true;
      }
    }
    return false;
  }

  private boolean methodBindingsNeedFinder() {
    for (ViewBindings viewBindings : viewIdMap.values()) {
      for (Map.Entry<ListenerClass, Map<ListenerMethod, Set<MethodViewBinding>>> entry
          : viewBindings.getMethodBindings().entrySet()) {
        Map<ListenerMethod, Set<MethodViewBinding>> methodBindings = entry.getValue();
        for (ListenerMethod method : getListenerMethods(entry.getKey())) {
          if (methodBindings.containsKey(method)) {
            String[] parameterTypes = method.parameters();
            for (MethodViewBinding methodViewBinding : methodBindings.get(method)) {
              for (Parameter parameter : methodViewBinding.getParameters()) {
                if (parameter.requiresCast(parameterTypes[parameter.getListenerPosition()])) {
                  return true;
                }
              }
            }
          }
        }
      }
    }
    return false;
  }

  private boolean hasFieldBindings() {
    for (ViewBindings viewBindings : viewIdMap.values()) {
      if (viewBindings.getFieldBinding() != null) {
        return true;
      }
    }
    return !collectionBindings.isEmpty();
  }

  private boolean bindNeedsFinder() {
    return hasViewBindings() //
        || hasParentBinding() && parentBinding.bindNeedsFinder();
  }

  private boolean bindNeedsResources() {
    return hasResourceBindings() //
        || hasParentBinding() && parentBinding.bindNeedsResources();
  }

  private boolean bindNeedsTheme() {
    return hasResourceBindings() && hasResourceBindingsNeedingTheme() //
        || hasParentBinding() && parentBinding.bindNeedsTheme();
  }

  private boolean bindNeedsViewLocal() {
    for (ViewBindings viewBindings : viewIdMap.values()) {
      if (viewBindings.requiresLocal()) {
        return true;
      }
    }
    return false;
  }

  private static boolean requiresCast(TypeName type) {
    return !VIEW_TYPE.equals(type.toString());
  }

  @Override public String toString() {
    return binderClassName.toString();
  }
}
