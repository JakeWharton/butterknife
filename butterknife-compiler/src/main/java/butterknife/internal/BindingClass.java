package butterknife.internal;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.view.View;
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

import static butterknife.internal.ButterKnifeProcessor.VIEW_TYPE;
import static java.util.Collections.singletonList;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

final class BindingClass {
  private static final ClassName FINDER = ClassName.get("butterknife", "ButterKnife", "Finder");
  private static final ClassName VIEW_BINDER =
      ClassName.get("butterknife", "ButterKnife", "ViewBinder");

  private final Map<Integer, ViewBindings> viewIdMap = new LinkedHashMap<>();
  private final Map<FieldCollectionViewBinding, int[]> collectionBindings = new LinkedHashMap<>();
  private final List<FieldBitmapBinding> bitmapBindings = new ArrayList<>();
  private final List<FieldResourceBinding> resourceBindings = new ArrayList<>();
  private final String classPackage;
  private final String className;
  private final String targetClass;
  private String parentViewBinder;

  BindingClass(String classPackage, String className, String targetClass) {
    this.classPackage = classPackage;
    this.className = className;
    this.targetClass = targetClass;
  }

  void addBitmap(FieldBitmapBinding binding) {
    bitmapBindings.add(binding);
  }

  void addField(int id, FieldViewBinding binding) {
    getOrCreateViewBindings(id).addFieldBinding(binding);
  }

  void addFieldCollection(int[] ids, FieldCollectionViewBinding binding) {
    collectionBindings.put(binding, ids);
  }

  boolean addMethod(int id, ListenerClass listener, ListenerMethod method,
      MethodViewBinding binding) {
    ViewBindings viewBindings = getOrCreateViewBindings(id);
    if (viewBindings.hasMethodBinding(listener, method)
        && !"void".equals(method.returnType())) {
      return false;
    }
    viewBindings.addMethodBinding(listener, method, binding);
    return true;
  }

  void addResource(FieldResourceBinding binding) {
    resourceBindings.add(binding);
  }

  void setParentViewBinder(String parentViewBinder) {
    this.parentViewBinder = parentViewBinder;
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

    if (parentViewBinder != null) {
      result.superclass(ParameterizedTypeName.get(ClassName.bestGuess(parentViewBinder),
          TypeVariableName.get("T")));
    } else {
      result.addSuperinterface(ParameterizedTypeName.get(VIEW_BINDER, TypeVariableName.get("T")));
    }

    result.addMethod(createBindMethod());
    result.addMethod(createUnbindMethod());

    return JavaFile.builder(classPackage, result.build())
        .addFileComment("Generated code from Butter Knife. Do not modify!")
        .build();
  }

  private MethodSpec createBindMethod() {
    MethodSpec.Builder result = MethodSpec.methodBuilder("bind")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(FINDER, "finder", FINAL)
        .addParameter(TypeVariableName.get("T"), "target", FINAL)
        .addParameter(Object.class, "source");

    // Emit a call to the superclass binder, if any.
    if (parentViewBinder != null) {
      result.addStatement("super.bind(finder, target, source)");
    }

    if (!viewIdMap.isEmpty() || !collectionBindings.isEmpty()) {
      // Local variable in which all views will be temporarily stored.
      result.addStatement("$T view", View.class);

      // Loop over each view bindings and emit it.
      for (ViewBindings bindings : viewIdMap.values()) {
        addViewBindings(result, bindings);
      }

      // Loop over each collection binding and emit it.
      for (Map.Entry<FieldCollectionViewBinding, int[]> entry : collectionBindings.entrySet()) {
        emitCollectionBinding(result, entry.getKey(), entry.getValue());
      }
    }

    if (requiresResources()) {
      result.addStatement("$T res = finder.getContext(source).getResources()", Resources.class);

      if (!bitmapBindings.isEmpty()) {
        for (FieldBitmapBinding binding : bitmapBindings) {
          result.addStatement("target.$L = $T.decodeResource(res, $L)", binding.getName(),
              BitmapFactory.class, binding.getId());
        }
      }

      if (!resourceBindings.isEmpty()) {
        for (FieldResourceBinding binding : resourceBindings) {
          result.addStatement("target.$L = res.$L($L)", binding.getName(), binding.getMethod(),
              binding.getId());
        }
      }
    }

    return result.build();
  }

  private void emitCollectionBinding(MethodSpec.Builder result, FieldCollectionViewBinding binding,
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

    result.addStatement("target.$L = $T.$L($L)", binding.getName(), FINDER, ofName,
        builder.build());
  }

  private void addViewBindings(MethodSpec.Builder result, ViewBindings bindings) {
    List<ViewBinding> requiredViewBindings = bindings.getRequiredBindings();
    if (requiredViewBindings.isEmpty()) {
      result.addStatement("view = finder.findOptionalView(source, $L, null)", bindings.getId());
    } else {
      if (bindings.getId() == View.NO_ID) {
        result.addStatement("view = target", bindings.getId());
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

  private MethodSpec createUnbindMethod() {
    MethodSpec.Builder result = MethodSpec.methodBuilder("unbind")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(TypeVariableName.get("T"), "target");

    if (parentViewBinder != null) {
      result.addStatement("super.unbind(target)");
    }
    for (ViewBindings bindings : viewIdMap.values()) {
      for (FieldViewBinding fieldBinding : bindings.getFieldBindings()) {
        result.addStatement("target.$L = null", fieldBinding.getName());
      }
    }
    for (FieldCollectionViewBinding fieldCollectionBinding : collectionBindings.keySet()) {
      result.addStatement("target.$L = null", fieldCollectionBinding.getName());
    }

    return result.build();
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

  private boolean requiresResources() {
    return !bitmapBindings.isEmpty() || !resourceBindings.isEmpty();
  }
}
