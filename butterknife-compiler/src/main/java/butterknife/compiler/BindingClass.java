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

final class BindingClass {
  private static final ClassName UTILS = ClassName.get("butterknife.internal", "Utils");
  private static final ClassName VIEW = ClassName.get("android.view", "View");
  private static final ClassName CONTEXT = ClassName.get("android.content", "Context");
  private static final ClassName RESOURCES = ClassName.get("android.content.res", "Resources");
  private static final ClassName THEME = RESOURCES.nestedClass("Theme");
  private static final ClassName UNBINDER = ClassName.get("butterknife", "Unbinder");
  private static final ClassName BITMAP_FACTORY =
      ClassName.get("android.graphics", "BitmapFactory");

  private final Map<Id, ViewBindings> viewIdMap = new LinkedHashMap<>();
  private final Map<FieldCollectionViewBinding, List<Id>> collectionBindings =
      new LinkedHashMap<>();
  private final List<FieldBitmapBinding> bitmapBindings = new ArrayList<>();
  private final List<FieldDrawableBinding> drawableBindings = new ArrayList<>();
  private final List<FieldResourceBinding> resourceBindings = new ArrayList<>();
  private final boolean isFinal;
  private final TypeName targetTypeName;
  private final ClassName bindingClassName;
  private BindingClass parentBinding;

  BindingClass(TypeName targetTypeName, ClassName bindingClassName, boolean isFinal) {
    this.isFinal = isFinal;
    this.targetTypeName = targetTypeName;
    this.bindingClassName = bindingClassName;
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

  JavaFile brewJava() {
    return JavaFile.builder(bindingClassName.packageName(), createBindingClass())
        .addFileComment("Generated code from Butter Knife. Do not modify!")
        .build();
  }

  private TypeSpec createBindingClass() {
    TypeSpec.Builder result = TypeSpec.classBuilder(bindingClassName.simpleName())
        .addModifiers(PUBLIC);

    TypeName targetType;
    if (isFinal) {
      result.addModifiers(FINAL);
      targetType = targetTypeName;
    } else {
      targetType = TypeVariableName.get("T");
      result.addTypeVariable(TypeVariableName.get("T", targetTypeName));
    }

    if (hasParentBinding()) {
      result.superclass(ParameterizedTypeName.get(getParentBinding(), targetType));
    } else {
      result.addSuperinterface(UNBINDER);
      result.addField(targetType, "target", isFinal ? PRIVATE : PROTECTED);
    }

    if (!bindNeedsView()) {
      // Add a delegating constructor with a target type + view signature for reflective use.
      result.addMethod(createBindingViewDelegateConstructor(targetType));
    }
    result.addMethod(createBindingConstructor(targetType));

    if (hasViewBindings() || !hasParentBinding()) {
      result.addMethod(createBindingUnbindMethod(result, targetType));
    }

    return result.build();
  }

  private MethodSpec createBindingViewDelegateConstructor(TypeName targetType) {
    return MethodSpec.constructorBuilder()
        .addJavadoc("@deprecated Use {@link #$T($T, $T)} for direct creation.\n    "
                + "Only present for runtime invocation through {@code ButterKnife.bind()}.\n",
            bindingClassName, targetType, CONTEXT)
        .addAnnotation(Deprecated.class)
        .addModifiers(PUBLIC)
        .addParameter(targetType, "target")
        .addParameter(VIEW, "source")
        .addStatement(("this(target, source.getContext())"))
        .build();
  }

  private MethodSpec createBindingConstructor(TypeName targetType) {
    MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
        .addModifiers(PUBLIC);

    if (hasMethodBindings()) {
      constructor.addParameter(targetType, "target", FINAL);
    } else {
      constructor.addParameter(targetType, "target");
    }

    if (bindNeedsView()) {
      constructor.addParameter(VIEW, "source");
    } else {
      constructor.addParameter(CONTEXT, "context");
    }

    if (!hasParentBinding()) {
      constructor.addStatement("this.target = target");
    } else if (parentBinding.bindNeedsView()) {
      constructor.addStatement("super(target, source)");
    } else if (bindNeedsView()) {
      constructor.addStatement("super(target, source.getContext())");
    } else {
      constructor.addStatement("super(target, context)");
    }
    constructor.addCode("\n");

    if (hasUnqualifiedResourceBindings()) {
      // Aapt can change IDs out from underneath us, just suppress since all will work at runtime.
      constructor.addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
          .addMember("value", "$S", "ResourceType")
          .build());
    }

    if (hasViewBindings()) {
      if (bindNeedsViewLocal()) {
        // Local variable in which all views will be temporarily stored.
        constructor.addStatement("$T view", VIEW);
      }
      for (ViewBindings bindings : viewIdMap.values()) {
        addViewBindings(constructor, bindings);
      }
      for (Map.Entry<FieldCollectionViewBinding, List<Id>> entry : collectionBindings.entrySet()) {
        emitCollectionBinding(constructor, entry.getKey(), entry.getValue());
      }

      if (hasResourceBindings()) {
        constructor.addCode("\n");
      }
    }

    if (hasResourceBindings()) {
      boolean hasView = bindNeedsView();
      boolean needsSourceToContext = bindNeedsTheme() && hasView;
      if (needsSourceToContext) {
        constructor.addStatement("$T context = source.getContext()", CONTEXT);
      }
      constructor.addStatement("$T res = $N.getResources()", RESOURCES,
          needsSourceToContext || !hasView ? "context" : "source");
      if (bindNeedsTheme()) {
        constructor.addStatement("$T theme = context.getTheme()", THEME);
      }

      for (FieldBitmapBinding binding : bitmapBindings) {
        constructor.addStatement("target.$L = $T.decodeResource(res, $L)", binding.getName(),
            BITMAP_FACTORY, binding.getId().code);
      }

      for (FieldDrawableBinding binding : drawableBindings) {
        Id tintAttributeId = binding.getTintAttributeId();
        if (tintAttributeId.value != 0) {
          constructor.addStatement("target.$L = $T.getTintedDrawable(res, theme, $L, $L)",
              binding.getName(), UTILS, binding.getId().code, tintAttributeId.code);
        } else {
          constructor.addStatement("target.$L = $T.getDrawable(res, theme, $L)", binding.getName(),
              UTILS, binding.getId().code);
        }
      }

      for (FieldResourceBinding binding : resourceBindings) {
        if (binding.isThemeable()) {
          constructor.addStatement("target.$L = $T.$L(res, theme, $L)", binding.getName(),
              UTILS, binding.getMethod(), binding.getId().code);
        } else {
          constructor.addStatement("target.$L = res.$L($L)", binding.getName(), binding.getMethod(),
              binding.getId().code);
        }
      }
    }

    return constructor.build();
  }

  private MethodSpec createBindingUnbindMethod(TypeSpec.Builder bindingClass,
      TypeName targetType) {
    MethodSpec.Builder result = MethodSpec.methodBuilder("unbind")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC);
    boolean rootBindingWithFields = !hasParentBinding() && hasFieldBindings();
    if (hasFieldBindings() || rootBindingWithFields) {
      result.addStatement("$T target = this.target", targetType);
    }
    if (!hasParentBinding()) {
      String target = rootBindingWithFields ? "target" : "this.target";
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
        addFieldAndUnbindStatement(bindingClass, result, bindings);
      }
    }

    if (!hasParentBinding()) {
      result.addCode("\n");
      result.addStatement("this.target = null");
    }

    return result.build();
  }

  private void addFieldAndUnbindStatement(TypeSpec.Builder result, MethodSpec.Builder unbindMethod,
      ViewBindings bindings) {
    // Only add fields to the binding if there are method bindings.
    Map<ListenerClass, Map<ListenerMethod, Set<MethodViewBinding>>> classMethodBindings =
        bindings.getMethodBindings();
    if (classMethodBindings.isEmpty()) {
      return;
    }

    String fieldName = bindings.isBoundToRoot() ? "viewSource" : "view" + bindings.getId().value;
    result.addField(VIEW, fieldName, PRIVATE);

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

    unbindMethod.addStatement("$N = null", fieldName);

    if (needsNullChecked) {
      unbindMethod.endControlFlow();
    }
  }

  private String removerOrSetter(ListenerClass listenerClass, boolean requiresRemoval) {
    return requiresRemoval
        ? listenerClass.remover()
        : listenerClass.setter();
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

      boolean requiresCast = requiresCast(binding.getType());
      if (!requiresCast && !binding.isRequired()) {
        builder.add("source.findViewById($L)", ids.get(i).code);
      } else {
        builder.add("$T.find", UTILS);
        builder.add(binding.isRequired() ? "RequiredView" : "OptionalView");
        if (requiresCast) {
          builder.add("AsType");
        }
        builder.add("(source, $L", ids.get(i).code);
        if (binding.isRequired() || requiresCast) {
          builder.add(", $S", asHumanDescription(singletonList(binding)));
        }
        if (requiresCast) {
          builder.add(", $T.class", binding.getRawType());
        }
        builder.add(")");
      }
    }

    result.addStatement("target.$L = $T.$L($L)", binding.getName(), UTILS, ofName, builder.build());
  }

  private void addViewBindings(MethodSpec.Builder result, ViewBindings bindings) {
    if (bindings.isSingleFieldBinding()) {
      // Optimize the common case where there's a single binding directly to a field.
      FieldViewBinding fieldBinding = bindings.getFieldBinding();
      CodeBlock.Builder builder = CodeBlock.builder()
          .add("target.$L = ", fieldBinding.getName());

      boolean requiresCast = requiresCast(fieldBinding.getType());
      if (!requiresCast && !fieldBinding.isRequired()) {
        builder.add("source.findViewById($L)", bindings.getId().code);
      } else {
        builder.add("$T.find", UTILS);
        builder.add(fieldBinding.isRequired() ? "RequiredView" : "OptionalView");
        if (requiresCast) {
          builder.add("AsType");
        }
        builder.add("(source, $L", bindings.getId().code);
        if (fieldBinding.isRequired() || requiresCast) {
          builder.add(", $S", asHumanDescription(singletonList(fieldBinding)));
        }
        if (requiresCast) {
          builder.add(", $T.class", fieldBinding.getRawType());
        }
        builder.add(")");
      }
      result.addStatement("$L", builder.build());
      return;
    }

    List<ViewBinding> requiredViewBindings = bindings.getRequiredBindings();
    if (requiredViewBindings.isEmpty()) {
      result.addStatement("view = source.findViewById($L)", bindings.getId().code);
    } else if (!bindings.isBoundToRoot()) {
      result.addStatement("view = $T.findRequiredView(source, $L, $S)", UTILS,
          bindings.getId().code, asHumanDescription(requiredViewBindings));
    }

    addFieldBindings(result, bindings);
    addMethodBindings(result, bindings);
  }

  private void addFieldBindings(MethodSpec.Builder result, ViewBindings bindings) {
    FieldViewBinding fieldBinding = bindings.getFieldBinding();
    if (fieldBinding != null) {
      if (requiresCast(fieldBinding.getType())) {
        result.addStatement("target.$L = $T.castView(view, $L, $S, $T.class)",
            fieldBinding.getName(), UTILS, bindings.getId().code,
            asHumanDescription(singletonList(fieldBinding)), fieldBinding.getRawType());
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

    // Add the view reference to the binding.
    String fieldName = "viewSource";
    String bindName = "source";
    if (!bindings.isBoundToRoot()) {
      fieldName = "view" + bindings.getId().value;
      bindName = "view";
    }
    result.addStatement("$L = $N", fieldName, bindName);

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
                builder.add("$T.<$T>castParam(p$L, $S, $L, $S, $L)", UTILS, parameter.getType(),
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

      boolean requiresRemoval = listener.remover().length() != 0;
      String listenerField = null;
      if (requiresRemoval) {
        TypeName listenerClassName = bestGuess(listener.type());
        listenerField = fieldName + ((ClassName) listenerClassName).simpleName();
        result.addStatement("$L = $L", listenerField, callback.build());
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

  private static List<ListenerMethod> getListenerMethods(ListenerClass listener) {
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

  private static TypeName bestGuess(String type) {
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

  /** Return the nearest binding class from this type's parents. */
  private ClassName getParentBinding() {
    return parentBinding.bindingClassName;
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

  private boolean hasFieldBindings() {
    for (ViewBindings viewBindings : viewIdMap.values()) {
      if (viewBindings.getFieldBinding() != null) {
        return true;
      }
    }
    return !collectionBindings.isEmpty();
  }

  /** True if this binding requires a view. Otherwise only a context is needed. */
  private boolean bindNeedsView() {
    return hasViewBindings() //
        || hasParentBinding() && parentBinding.bindNeedsView();
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
    return bindingClassName.toString();
  }
}
