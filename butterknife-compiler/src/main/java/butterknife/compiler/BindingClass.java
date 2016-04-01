package butterknife.compiler;

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

import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;

import static butterknife.compiler.ButterKnifeProcessor.NO_ID;
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
  private static final ClassName BUTTERKNIFE = ClassName.get("butterknife", "ButterKnife");
  private static final ClassName UNBINDER = ClassName.get("butterknife", "Unbinder");
  private static final ClassName BITMAP_FACTORY =
      ClassName.get("android.graphics", "BitmapFactory");
  public static final String UNBINDER_SIMPLE_NAME = "InnerUnbinder";

  private final Map<Integer, ViewBindings> viewIdMap = new LinkedHashMap<>();
  private final Map<FieldCollectionViewBinding, int[]> collectionBindings = new LinkedHashMap<>();
  private final List<FieldBitmapBinding> bitmapBindings = new ArrayList<>();
  private final List<FieldDrawableBinding> drawableBindings = new ArrayList<>();
  private final List<FieldResourceBinding> resourceBindings = new ArrayList<>();
  private final Set<BindingClass> descendantBindingClasses = new LinkedHashSet<>();
  private final String classPackage;
  private final String className;
  private final String targetClass;
  private final String classFqcn;
  private BindingClass parentBinding;
  private ClassName unbinderClassName;  // If this is null'd out, it has no unbinder and uses NOP.
  private ClassName highestUnbinderClassName; // If this is null'd out, there is no parent unbinder.

  BindingClass(String classPackage, String className, String targetClass, String classFqcn) {
    this.classPackage = classPackage;
    this.className = className;
    this.targetClass = targetClass;
    this.classFqcn = classFqcn;

    // Default to this, but this can be null'd out by the processor before we brew if it's not
    // necessary.
    this.unbinderClassName = ClassName.get(classPackage, className, UNBINDER_SIMPLE_NAME);
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
    TypeSpec.Builder result = TypeSpec.classBuilder(className)
        .addModifiers(PUBLIC)
        .addTypeVariable(TypeVariableName.get("T", ClassName.bestGuess(targetClass)));

    if (hasParentBinding()) {
      result.superclass(ParameterizedTypeName.get(ClassName.bestGuess(parentBinding.classFqcn),
          TypeVariableName.get("T")));
    } else {
      result.addSuperinterface(ParameterizedTypeName.get(VIEW_BINDER, TypeVariableName.get("T")));
    }

    result.addMethod(createBindMethod());

    if (hasUnbinder() && hasViewBindings()) {
      // Create unbinding class.
      result.addType(createUnbinderClass());
      // Now we need to provide child classes to access and override unbinder implementations.
      createUnbinderCreateUnbinderMethod(result);
    }

    return JavaFile.builder(classPackage, result.build())
        .addFileComment("Generated code from Butter Knife. Do not modify!")
        .build();
  }

  private TypeSpec createUnbinderClass() {
    TypeName generic = TypeVariableName.get("T");
    TypeSpec.Builder result =
        TypeSpec.classBuilder(unbinderClassName.simpleName())
            .addModifiers(PROTECTED, STATIC)
            .addTypeVariable(TypeVariableName.get("T", ClassName.bestGuess(targetClass)));

    if (hasParentBinding() && parentBinding.hasUnbinder()) {
      result.superclass(ParameterizedTypeName.get(
          parentBinding.getUnbinderClassName(), generic));
    } else {
      result.addSuperinterface(UNBINDER);
      result.addField(generic, "target", PRIVATE);
    }

    result.addMethod(createUnbinderConstructor(generic));
    if (!hasParentBinding() || !parentBinding.hasUnbinder()) {
      result.addMethod(createUnbindInterfaceMethod());
    }
    result.addMethod(createUnbindMethod(result, generic));

    return result.build();
  }

  private MethodSpec createUnbinderConstructor(TypeName targetType) {
    MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
        .addModifiers(PROTECTED)
        .addParameter(targetType, "target");
    if (hasParentBinding() && parentBinding.hasUnbinder()) {
      constructor.addStatement("super(target)");
    } else {
      constructor.addStatement("this.$1N = $1N", "target");
    }
    return constructor.build();
  }

  private MethodSpec createUnbindInterfaceMethod() {
    return MethodSpec.methodBuilder("unbind")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC, FINAL)
        .addStatement("if (target == null) throw new $T($S)", IllegalStateException.class,
            "Bindings already cleared.")
        .addStatement("unbind(target)")
        .addStatement("target = null")
        .build();
  }

  private MethodSpec createUnbindMethod(TypeSpec.Builder unbinderClass, TypeName targetType) {
    MethodSpec.Builder result = MethodSpec.methodBuilder("unbind")
        .addModifiers(PROTECTED)
        .addParameter(targetType, "target");

    if (hasParentBinding() && parentBinding.hasUnbinder()) {
      result.addAnnotation(Override.class);
      result.addStatement("super.unbind(target)");
    }

    for (ViewBindings bindings : viewIdMap.values()) {
      addFieldAndUnbindStatement(unbinderClass, result, bindings);
      for (FieldViewBinding fieldBinding : bindings.getFieldBindings()) {
        result.addStatement("target.$L = null", fieldBinding.getName());
      }
    }

    for (FieldCollectionViewBinding fieldCollectionBinding : collectionBindings.keySet()) {
      result.addStatement("target.$L = null", fieldCollectionBinding.getName());
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
      unbindMethod.beginControlFlow("if ($L != null)", fieldName);
    }

    for (ListenerClass listenerClass : classMethodBindings.keySet()) {
      if (!VIEW_TYPE.equals(listenerClass.targetType())) {
        unbindMethod.addStatement("(($T) $L).$L(null)", bestGuess(listenerClass.targetType()),
            fieldName, listenerClass.setter());
      } else {
        unbindMethod.addStatement("$L.$L(null)", fieldName, listenerClass.setter());
      }
    }

    if (needsNullChecked) {
      unbindMethod.endControlFlow();
    }
  }

  private void createUnbinderCreateUnbinderMethod(TypeSpec.Builder viewBindingClass) {
    // Create type variable InnerUnbinder<T>
    TypeName returnType = ParameterizedTypeName.get(
        unbinderClassName, TypeVariableName.get("T"));

    MethodSpec.Builder createUnbinder = MethodSpec.methodBuilder("createUnbinder")
        .addModifiers(PROTECTED)
        .returns(returnType)
        .addParameter(TypeVariableName.get("T"), "target")
        .addStatement("return new $T($L)", unbinderClassName, "target");

    if (hasParentBinding() && parentBinding.hasUnbinder()) {
      createUnbinder.addAnnotation(Override.class);
    }
    viewBindingClass.addMethod(createUnbinder.build());
  }

  private MethodSpec createBindMethod() {
    MethodSpec.Builder result = MethodSpec.methodBuilder("bind")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(UNBINDER)
        .addParameter(FINDER, "finder", FINAL)
        .addParameter(TypeVariableName.get("T"), "target", FINAL)
        .addParameter(Object.class, "source");

    if (hasResourceBindings()) {
      // Aapt can change IDs out from underneath us, just suppress since all will work at runtime.
      result.addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
          .addMember("value", "$S", "ResourceType")
          .build());
    }

    // Emit a call to the superclass binder, if any.
    if (hasParentBinding()) {
      if (hasViewBindings()) {
        if (highestUnbinderClassName != unbinderClassName) {
          // This has an unbinder class and there exists an unbinder class farther up, so use super
          // and let the super implementation create it for us.
          result.addStatement("$T unbinder = ($T) super.bind(finder, target, source)",
              unbinderClassName,
              unbinderClassName);
        } else {
          // This has an unbinder class and there is no implementation higher up, so we'll call
          // super but ignore the result since it's just the NOP. Instead, create the unbinder here
          // for our implementation and any descendant classes.
          result.addStatement("super.bind(finder, target, source)");
          result.addStatement("$T unbinder = createUnbinder(target)", unbinderClassName);
        }
      } else {
        // This has no unbinder class, just defer to super (which could be NOP or real,
        // we don't care).
        result.addStatement("$T unbinder = super.bind(finder, target, source)", UNBINDER);
      }
    } else if (hasViewBindings()) {
      // This is a top-level class but we do have an unbinder class, so no need to call super but
      // go ahead and create our unbinder.
      result.addStatement("$T unbinder = createUnbinder(target)", unbinderClassName);
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
    }

    if (hasResourceBindings()) {
      if (hasResourceBindingsNeedingTheme()) {
        result.addStatement("$T context = finder.getContext(source)", CONTEXT);
        result.addStatement("$T res = context.getResources()", RESOURCES);
        result.addStatement("$T theme = context.getTheme()", THEME);
      } else {
        result.addStatement("$T res = finder.getContext(source).getResources()", RESOURCES);
      }

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

    // Finally return the unbinder.
    if (hasParentBinding() || hasViewBindings()) {
      result.addStatement("return unbinder");
    } else {
      result.addStatement("return $T.EMPTY", UNBINDER);
    }

    return result.build();
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
    if (hasUnbinder()) {
      result.addStatement("unbinder.$L = view", "view" + bindings.getUniqueIdSuffix());
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

      if (!VIEW_TYPE.equals(listener.targetType())) {
        result.addStatement("(($T) view).$L($L)", bestGuess(listener.targetType()),
            listener.setter(), callback.build());
      } else {
        result.addStatement("view.$L($L)", listener.setter(), callback.build());
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

  @Override
  public String toString() {
    return classFqcn;
  }
}
