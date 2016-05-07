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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Modifier;

import static butterknife.compiler.ButterKnifeProcessor.NO_ID;
import static butterknife.compiler.ButterKnifeProcessor.VIEW_TYPE;
import static java.util.Collections.singletonList;
import static javax.lang.model.element.Modifier.FINAL;
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
  private static final String UNBINDER_SIMPLE_NAME = "InnerUnbinder";
  private static final String BIND_TO_TARGET = "bindToTarget";

  private final Map<Integer, ViewBindings> viewIdMap = new LinkedHashMap<>();
  private final Map<FieldCollectionViewBinding, int[]> collectionBindings = new LinkedHashMap<>();
  private final List<FieldBitmapBinding> bitmapBindings = new ArrayList<>();
  private final List<FieldDrawableBinding> drawableBindings = new ArrayList<>();
  private final List<FieldResourceBinding> resourceBindings = new ArrayList<>();
  private final Set<BindingClass> descendantBindingClasses = new LinkedHashSet<>();
  private final boolean isFinal;
  private final TypeName targetTypeName;
  private final ClassName generatedClassName;
  private BindingClass parentBinding;
  private ClassName unbinderClassName;  // If this is null'd out, it has no unbinder and uses NOP.
  private ClassName highestUnbinderClassName; // If this is null'd out, there is no parent unbinder.

  BindingClass(TypeName targetTypeName, ClassName generatedClassName, boolean isFinal) {
    this.isFinal = isFinal;
    this.targetTypeName = targetTypeName;
    this.generatedClassName = generatedClassName;

    // Default to this, but this can be null'd out by the processor before we brew if it's not
    // necessary.
    this.unbinderClassName = generatedClassName.nestedClass(UNBINDER_SIMPLE_NAME);
  }

  void addDescendant(BindingClass bindingClass) {
    descendantBindingClasses.add(bindingClass);
  }

  void addBitmap(FieldBitmapBinding binding) {
    bitmapBindings.add(binding);
  }

  void addDrawable(FieldDrawableBinding binding) {
    drawableBindings.add(binding);
  }

  void addField(int id, FieldViewBinding binding) {
    getOrCreateViewBindings(id).addFieldBinding(binding);
  }

  void addFieldCollection(int[] ids, FieldCollectionViewBinding binding) {
    collectionBindings.put(binding, ids);
  }

  boolean addMethod(
      int id,
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

  ViewBindings getViewBinding(int id) {
    return viewIdMap.get(id);
  }

  private ViewBindings getOrCreateViewBindings(int id) {
    ViewBindings viewId = viewIdMap.get(id);
    if (viewId == null) {
      viewId = new ViewBindings(id);
      viewIdMap.put(id, viewId);
    }
    return viewId;
  }

  JavaFile brewJava() {
    TypeSpec.Builder result = TypeSpec.classBuilder(generatedClassName)
        .addModifiers(PUBLIC);
    if (isFinal) {
      result.addModifiers(Modifier.FINAL);
    } else {
      result.addTypeVariable(TypeVariableName.get("T", targetTypeName));
    }

    TypeName targetType = isFinal ? targetTypeName : TypeVariableName.get("T");
    if (hasParentBinding()) {
      result.superclass(ParameterizedTypeName.get(parentBinding.generatedClassName, targetType));
    } else {
      result.addSuperinterface(ParameterizedTypeName.get(VIEW_BINDER, targetType));
    }

    result.addMethod(createNewBindMethod(targetType));
    if (!isFinal) {
      result.addMethod(createNewBindToTargetMethod());
    }

    if (hasUnbinder() && hasViewBindings()) {
      // Create unbinding class.
      result.addType(createUnbinderClass(targetType));
    }

    return JavaFile.builder(generatedClassName.packageName(), result.build())
        .addFileComment("Generated code from Butter Knife. Do not modify!")
        .build();
  }

  private TypeSpec createUnbinderClass(TypeName targetType) {
    TypeSpec.Builder result = TypeSpec.classBuilder(unbinderClassName.simpleName())
            .addModifiers(PROTECTED, STATIC);
    if (isFinal) {
      result.addModifiers(Modifier.FINAL);
    } else {
      result.addTypeVariable(TypeVariableName.get("T", targetTypeName));
    }

    if (hasParentUnbinder()) {
      result.superclass(ParameterizedTypeName.get(
          parentBinding.getUnbinderClassName(), targetType));
    } else {
      result.addSuperinterface(UNBINDER);
      result.addField(targetType, "target", PROTECTED);
    }

    result.addMethod(createUnbinderConstructor(targetType));
    if (hasViewBindings()) {
      result.addMethod(createUnbindInterfaceMethod(result, targetType));
    }

    return result.build();
  }

  private MethodSpec createUnbinderConstructor(TypeName targetType) {
    MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
        .addModifiers(PROTECTED)
        .addParameter(targetType, "target");
    if (hasParentUnbinder()) {
      constructor.addStatement("super(target)");
    } else {
      constructor.addStatement("this.target = target");
    }
    return constructor.build();
  }

  private MethodSpec createUnbindInterfaceMethod(TypeSpec.Builder unbinderClass,
      TypeName targetType) {
    MethodSpec.Builder result = MethodSpec.methodBuilder("unbind")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC);
    if (!hasParentUnbinder() || hasFieldBindings()) {
      result.addStatement("$T target = this.target", targetType);
    }
    if (!hasParentUnbinder()) {
      result.addStatement("if (target == null) throw new $T($S)", IllegalStateException.class,
          "Bindings already cleared.");
    } else {
      result.addStatement("super.unbind()");
    }

    if (hasFieldBindings()) {
      result.addCode("\n");
      for (ViewBindings bindings : viewIdMap.values()) {
        for (FieldViewBinding fieldBinding : bindings.getFieldBindings()) {
          result.addStatement("target.$L = null", fieldBinding.getName());
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

    if (!hasParentUnbinder()) {
      result.addCode("\n");
      result.addStatement("this.target = null");
    }

    return result.build();
  }

  private void addFieldAndUnbindStatement(
      TypeSpec.Builder result,
      MethodSpec.Builder unbindMethod,
      ViewBindings bindings) {
    // Only add fields to the unbinder if there are method bindings.
    Map<ListenerClass, Map<ListenerMethod, Set<MethodViewBinding>>> classMethodBindings =
        bindings.getMethodBindings();
    if (classMethodBindings.isEmpty()) {
      return;
    }

    // Using unique view id for name uniqueness.
    String fieldName = "view" + bindings.getUniqueIdSuffix();
    result.addField(VIEW, fieldName);

    // We only need to emit the null check if there are zero required bindings.
    boolean needsNullChecked = bindings.getRequiredBindings().isEmpty();
    if (needsNullChecked) {
      unbindMethod.beginControlFlow("if ($N != null)", fieldName);
    }

    for (ListenerClass listenerClass : classMethodBindings.keySet()) {
      // We need to keep a reference to the listener
      // in case we need to unbind it via a remove method.
      boolean requiresRemoval = !listenerClass.remover().isEmpty();
      String listenerField = "null";
      if (requiresRemoval) {
        TypeName listenerClassName = bestGuess(listenerClass.type());
        listenerField = fieldName + ((ClassName) listenerClassName).simpleName();
        result.addField(listenerClassName, listenerField);
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

  private MethodSpec createNewBindMethod(TypeName targetType) {
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
    boolean needsUnbinder = bindNeedsUnbinder();

    if (needsResources) {
      if (needsTheme) {
        result.addStatement("$T context = finder.getContext(source)", CONTEXT);
        result.addStatement("$T res = context.getResources()", RESOURCES);
        result.addStatement("$T theme = context.getTheme()", THEME);
      } else {
        result.addStatement("$T res = finder.getContext(source).getResources()", RESOURCES);
      }
    }

    if (needsUnbinder) {
      result.addStatement("$1T unbinder = new $1T(target)", unbinderClassName);
    }

    if (isFinal) {
      if (needsResources || needsUnbinder) {
        result.addCode("\n");
      }
      generateBindViewBody(result);
      result.addCode("\n");
    } else {
      CodeBlock.Builder invoke = CodeBlock.builder().add("$N(target", BIND_TO_TARGET);
      if (needsFinder) invoke.add(", finder, source");
      if (needsResources) invoke.add(", res");
      if (needsTheme) invoke.add(", theme");
      if (needsUnbinder) invoke.add(", unbinder");
      result.addStatement("$L", invoke.add(")").build());
    }

    if (needsUnbinder) {
      result.addStatement("return unbinder");
    } else if (hasUnbinder()) {
      result.addStatement("return new $T(target)", unbinderClassName);
    } else {
      result.addStatement("return $T.EMPTY", UNBINDER);
    }

    return result.build();
  }

  private MethodSpec createNewBindToTargetMethod() {
    MethodSpec.Builder result = MethodSpec.methodBuilder(BIND_TO_TARGET)
        .addModifiers(PROTECTED, STATIC);

    if (hasMethodBindings()) {
      result.addParameter(targetTypeName, "target", FINAL);
    } else {
      result.addParameter(targetTypeName, "target");
    }

    if (bindNeedsFinder()) {
      result.addParameter(FINDER, "finder")
          .addParameter(Object.class, "source");
    }
    if (bindNeedsResources()) {
      result.addParameter(RESOURCES, "res");
    }
    if (bindNeedsTheme()) {
      result.addParameter(THEME, "theme");
    }
    if (bindNeedsUnbinder()) {
      result.addParameter(unbinderClassName, "unbinder");
    }

    generateBindViewBody(result);

    return result.build();
  }

  private void generateBindViewBody(MethodSpec.Builder result) {
    if (hasResourceBindings()) {
      // Aapt can change IDs out from underneath us, just suppress since all will work at runtime.
      result.addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
          .addMember("value", "$S", "ResourceType")
          .build());
    }

    if (hasParentBinding()) {
      CodeBlock.Builder invoke = CodeBlock.builder() //
          .add("$T.$N(target", parentBinding.generatedClassName, BIND_TO_TARGET);
      if (parentBinding.bindNeedsFinder()) invoke.add(", finder, source");
      if (parentBinding.bindNeedsResources()) invoke.add(", res");
      if (parentBinding.bindNeedsTheme()) invoke.add(", theme");
      if (parentBinding.bindNeedsUnbinder()) invoke.add(", unbinder");
      result.addStatement("$L", invoke.add(")").build());
      result.addCode("\n");
    }

    if (!viewIdMap.isEmpty() || !collectionBindings.isEmpty()) {
      // Local variable in which all views will be temporarily stored.
      result.addStatement("$T view", VIEW);

      // Loop over each view bindings and emit it.
      for (ViewBindings bindings : viewIdMap.values()) {
        addViewBindings(result, bindings);
      }

      // Loop over each collection binding and emit it.
      for (Map.Entry<FieldCollectionViewBinding, int[]> entry : collectionBindings.entrySet()) {
        emitCollectionBinding(result, entry.getKey(), entry.getValue());
      }

      if (hasResourceBindings()) {
        result.addCode("\n");
      }
    }

    if (hasResourceBindings()) {
      for (FieldBitmapBinding binding : bitmapBindings) {
        result.addStatement("target.$L = $T.decodeResource(res, $L)", binding.getName(),
            BITMAP_FACTORY, binding.getId());
      }

      for (FieldDrawableBinding binding : drawableBindings) {
        int tintAttributeId = binding.getTintAttributeId();
        if (tintAttributeId != 0) {
          result.addStatement("target.$L = $T.getTintedDrawable(res, theme, $L, $L)",
              binding.getName(), UTILS, binding.getId(), tintAttributeId);
        } else {
          result.addStatement("target.$L = $T.getDrawable(res, theme, $L)", binding.getName(),
              UTILS, binding.getId());
        }
      }

      for (FieldResourceBinding binding : resourceBindings) {
        // TODO being themeable is poor correlation to the need to use Utils.
        if (binding.isThemeable()) {
          result.addStatement("target.$L = $T.$L(res, theme, $L)", binding.getName(),
              UTILS, binding.getMethod(), binding.getId());
        } else {
          result.addStatement("target.$L = res.$L($L)", binding.getName(), binding.getMethod(),
              binding.getId());
        }
      }
    }
  }

  private void emitCollectionBinding(
      MethodSpec.Builder result,
      FieldCollectionViewBinding binding,
      int[] ids) {
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
    for (int i = 0; i < ids.length; i++) {
      if (i > 0) {
        builder.add(", ");
      }
      String findMethod = binding.isRequired() ? "findRequiredView" : "findOptionalView";
      builder.add("\nfinder.<$T>$L(source, $L, $S)", binding.getType(), findMethod, ids[i],
          asHumanDescription(singletonList(binding)));
    }

    result.addStatement("target.$L = $T.$L($L)", binding.getName(), UTILS, ofName, builder.build());
  }

  private void addViewBindings(MethodSpec.Builder result, ViewBindings bindings) {
    List<ViewBinding> requiredViewBindings = bindings.getRequiredBindings();
    if (requiredViewBindings.isEmpty()) {
      result.addStatement("view = finder.findOptionalView(source, $L, null)", bindings.getId());
    } else {
      if (bindings.getId() == NO_ID) {
        result.addStatement("view = target");
      } else {
        result.addStatement("view = finder.findRequiredView(source, $L, $S)", bindings.getId(),
            asHumanDescription(requiredViewBindings));
      }
    }

    addFieldBindings(result, bindings);
    addMethodBindings(result, bindings);
  }

  private void addFieldBindings(MethodSpec.Builder result, ViewBindings bindings) {
    Collection<FieldViewBinding> fieldBindings = bindings.getFieldBindings();
    for (FieldViewBinding fieldBinding : fieldBindings) {
      if (fieldBinding.requiresCast()) {
        result.addStatement("target.$L = finder.castView(view, $L, $S)", fieldBinding.getName(),
            bindings.getId(), asHumanDescription(fieldBindings));
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
    String fieldName = "view" + bindings.getUniqueIdSuffix();
    if (hasUnbinder()) {
      result.addStatement("unbinder.$L = view", fieldName);
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
                builder.add("finder.<$T>castParam(p$L, $S, $L, $S, $L)\n", parameter.getType(),
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

      boolean requiresRemoval = hasUnbinder() && listener.remover().length() != 0;
      String listenerField = null;
      if (requiresRemoval) {
        TypeName listenerClassName = bestGuess(listener.type());
        listenerField = fieldName + ((ClassName) listenerClassName).simpleName();
        result.addStatement("unbinder.$L = $L", listenerField, callback.build());
      }

      if (!VIEW_TYPE.equals(listener.targetType())) {
        result.addStatement("(($T) view).$L($L)", bestGuess(listener.targetType()),
            listener.setter(), requiresRemoval ? "unbinder." + listenerField : callback.build());
      } else {
        result.addStatement("view.$L($L)", listener.setter(),
            requiresRemoval ? "unbinder." + listenerField : callback.build());
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

  boolean hasUnbinder() {
    return unbinderClassName != null;
  }

  void setHighestUnbinderClassName(ClassName className) {
    this.highestUnbinderClassName = className;
  }

  ClassName getHighestUnbinderClassName() {
    return this.highestUnbinderClassName;
  }

  void setUnbinderClassName(ClassName className) {
    unbinderClassName = className;
  }

  ClassName getUnbinderClassName() {
    return unbinderClassName;
  }

  BindingClass getParentBinding() {
    return parentBinding;
  }

  boolean hasParentBinding() {
    return parentBinding != null;
  }

  private boolean hasParentUnbinder() {
    return hasParentBinding() && parentBinding.hasUnbinder();
  }

  private boolean hasResourceBindings() {
    return !(bitmapBindings.isEmpty() && drawableBindings.isEmpty() && resourceBindings.isEmpty());
  }

  boolean hasViewBindings() {
    return !viewIdMap.isEmpty() || !collectionBindings.isEmpty();
  }

  Iterable<BindingClass> getDescendants() {
    return descendantBindingClasses;
  }

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

  private boolean bindNeedsUnbinder() {
    return hasUnbinder() && hasMethodBindings() //
        || hasParentBinding() && parentBinding.bindNeedsUnbinder();
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
      if (!viewBindings.getFieldBindings().isEmpty()) {
        return true;
      }
    }
    return !collectionBindings.isEmpty();
  }

  @Override public String toString() {
    return generatedClassName.toString();
  }
}
