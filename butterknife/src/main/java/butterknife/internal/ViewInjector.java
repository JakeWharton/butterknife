package butterknife.internal;

import android.view.View;
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

final class ViewInjector {
  private final Map<Integer, ViewInjection> viewIdMap = new LinkedHashMap<Integer, ViewInjection>();
  private final Map<CollectionBinding, int[]> collectionBindings =
      new LinkedHashMap<CollectionBinding, int[]>();
  private final String classPackage;
  private final String className;
  private final String targetClass;
  private String parentInjector;

  ViewInjector(String classPackage, String className, String targetClass) {
    this.classPackage = classPackage;
    this.className = className;
    this.targetClass = targetClass;
  }

  void addView(int id, ViewBinding binding) {
    getOrCreateViewInjection(id).addViewBinding(binding);
  }

  boolean addListener(int id, ListenerClass listener, ListenerMethod method,
      ListenerBinding binding) {
    ViewInjection viewInjection = getOrCreateViewInjection(id);
    if (viewInjection.hasListenerBinding(listener, method)
        && !"void".equals(method.returnType())) {
      return false;
    }
    viewInjection.addListenerBinding(listener, method, binding);
    return true;
  }

  void addCollection(int[] ids, CollectionBinding binding) {
    collectionBindings.put(binding, ids);
  }

  void setParentInjector(String parentInjector) {
    this.parentInjector = parentInjector;
  }

  ViewInjection getViewInjection(int id) {
    return viewIdMap.get(id);
  }

  private ViewInjection getOrCreateViewInjection(int id) {
    ViewInjection viewId = viewIdMap.get(id);
    if (viewId == null) {
      viewId = new ViewInjection(id);
      viewIdMap.put(id, viewId);
    }
    return viewId;
  }

  String getFqcn() {
    return classPackage + "." + className;
  }

  String brewJava() {
    StringBuilder builder = new StringBuilder();
    builder.append("// Generated code from Butter Knife. Do not modify!\n");
    builder.append("package ").append(classPackage).append(";\n\n");

    builder.append("import android.view.View;\n");
    builder.append("import butterknife.ButterKnife.Finder;\n");
    if (parentInjector == null) {
      builder.append("import butterknife.ButterKnife.Injector;\n");
    }
    builder.append('\n');

    builder.append("public class ").append(className);
    builder.append("<T extends ").append(targetClass).append(">");

    if (parentInjector != null) {
      builder.append(" extends ").append(parentInjector).append("<T>");
    } else {
      builder.append(" implements Injector<T>");
    }
    builder.append(" {\n");

    emitInject(builder);
    builder.append('\n');
    emitReset(builder);

    builder.append("}\n");
    return builder.toString();
  }

  private void emitInject(StringBuilder builder) {
    builder.append("  @Override ")
        .append("public void inject(Finder finder, final T target, Object source) {\n");

    // Emit a call to the superclass injector, if any.
    if (parentInjector != null) {
      builder.append("    super.inject(finder, target, source);\n\n");
    }

    // Local variable in which all views will be temporarily stored.
    builder.append("    View view;\n");

    // Loop over each view injection and emit it.
    for (ViewInjection injection : viewIdMap.values()) {
      emitViewInjection(builder, injection);
    }

    // Loop over each collection binding and emit it.
    for (Map.Entry<CollectionBinding, int[]> entry : collectionBindings.entrySet()) {
      emitCollectionBinding(builder, entry.getKey(), entry.getValue());
    }

    builder.append("  }\n");
  }

  private void emitCollectionBinding(StringBuilder builder, CollectionBinding binding, int[] ids) {
    builder.append("    target.").append(binding.getName()).append(" = ");

    switch (binding.getKind()) {
      case ARRAY:
        builder.append("Finder.arrayOf(");
        break;
      case LIST:
        builder.append("Finder.listOf(");
        break;
      default:
        throw new IllegalStateException("Unknown kind: " + binding.getKind());
    }

    for (int i = 0; i < ids.length; i++) {
      if (i > 0) {
        builder.append(',');
      }
      builder.append("\n        ");
      emitCastIfNeeded(builder, binding.getType());
      if (binding.isRequired()) {
        builder.append("finder.findRequiredView(source, ")
            .append(ids[i])
            .append(", \"")
            .append(binding.getName())
            .append("\")");
      } else {
        builder.append("finder.findOptionalView(source, ")
            .append(ids[i])
            .append(")");
      }
    }

    builder.append("\n    );");
  }

  private void emitViewInjection(StringBuilder builder, ViewInjection injection) {
    builder.append("    view = ");

    List<Binding> requiredBindings = injection.getRequiredBindings();
    if (requiredBindings.isEmpty()) {
      builder.append("finder.findOptionalView(source, ")
          .append(injection.getId())
          .append(");\n");
    } else {
      if (injection.getId() == View.NO_ID) {
        builder.append("target;\n");
      } else {
        builder.append("finder.findRequiredView(source, ")
            .append(injection.getId())
            .append(", \"");
        emitHumanDescription(builder, requiredBindings);
        builder.append("\");\n");
      }
    }

    emitViewBindings(builder, injection);
    emitListenerBindings(builder, injection);
  }

  private void emitViewBindings(StringBuilder builder, ViewInjection injection) {
    Collection<ViewBinding> viewBindings = injection.getViewBindings();
    if (viewBindings.isEmpty()) {
      return;
    }

    for (ViewBinding viewBinding : viewBindings) {
      builder.append("    target.")
          .append(viewBinding.getName())
          .append(" = ");
      emitCastIfNeeded(builder, viewBinding.getType());
      builder.append("view;\n");
    }
  }

  private void emitListenerBindings(StringBuilder builder, ViewInjection injection) {
    Map<ListenerClass, Map<ListenerMethod, Set<ListenerBinding>>> bindings =
        injection.getListenerBindings();
    if (bindings.isEmpty()) {
      return;
    }

    String extraIndent = "";

    // We only need to emit the null check if there are zero required bindings.
    boolean needsNullChecked = injection.getRequiredBindings().isEmpty();
    if (needsNullChecked) {
      builder.append("    if (view != null) {\n");
      extraIndent = "  ";
    }

    for (Map.Entry<ListenerClass, Map<ListenerMethod, Set<ListenerBinding>>> e
        : bindings.entrySet()) {
      ListenerClass listener = e.getKey();
      Map<ListenerMethod, Set<ListenerBinding>> methodBindings = e.getValue();

      // Emit: ((OWNER_TYPE) view).SETTER_NAME(
      boolean needsCast = !VIEW_TYPE.equals(listener.targetType());
      builder.append(extraIndent)
          .append("    ");
      if (needsCast) {
        builder.append("((").append(listener.targetType());
        if (listener.genericArguments() > 0) {
          builder.append('<');
          for (int i = 0; i < listener.genericArguments(); i++) {
            if (i > 0) {
              builder.append(", ");
            }
            builder.append('?');
          }
          builder.append('>');
        }
        builder.append(") ");
      }
      builder.append("view");
      if (needsCast) {
        builder.append(')');
      }
      builder.append('.')
          .append(listener.setter())
          .append("(\n");

      // Emit: new TYPE() {
      builder.append(extraIndent)
          .append("      new ")
          .append(listener.type())
          .append("() {\n");

      for (ListenerMethod method : getListenerMethods(listener)) {
        // Emit: @Override public RETURN_TYPE METHOD_NAME(
        builder.append(extraIndent)
            .append("        @Override public ")
            .append(method.returnType())
            .append(' ')
            .append(method.name())
            .append("(\n");

        // Emit listener method arguments, each on their own line.
        String[] parameterTypes = method.parameters();
        for (int i = 0, count = parameterTypes.length; i < count; i++) {
          builder.append(extraIndent)
              .append("          ")
              .append(parameterTypes[i])
              .append(" p")
              .append(i);
          if (i < count - 1) {
            builder.append(',');
          }
          builder.append('\n');
        }

        // Emit end of parameters, start of body.
        builder.append(extraIndent).append("        ) {\n");

        // Set up the return statement, if needed.
        builder.append(extraIndent).append("          ");
        boolean hasReturnType = !"void".equals(method.returnType());
        if (hasReturnType) {
          builder.append("return ");
        }

        if (methodBindings.containsKey(method)) {
          Set<ListenerBinding> set = methodBindings.get(method);
          Iterator<ListenerBinding> iterator = set.iterator();

          while (iterator.hasNext()) {
            ListenerBinding binding = iterator.next();
            builder.append("target.").append(binding.getName()).append('(');
            List<Parameter> parameters = binding.getParameters();
            String[] listenerParameters = method.parameters();
            for (int i = 0, count = parameters.size(); i < count; i++) {
              Parameter parameter = parameters.get(i);
              int listenerPosition = parameter.getListenerPosition();
              emitCastIfNeeded(builder, listenerParameters[listenerPosition], parameter.getType());
              builder.append('p').append(listenerPosition);
              if (i < count - 1) {
                builder.append(", ");
              }
            }
            builder.append(");");
            if (iterator.hasNext()) {
              builder.append("\n").append("          ");
            }
          }
        } else if (hasReturnType) {
          builder.append(method.defaultReturn()).append(';');
        }
        builder.append('\n');

        // Emit end of listener method.
        builder.append(extraIndent).append("        }\n");
      }

      // Emit end of listener class body and close the setter method call.
      builder.append(extraIndent).append("      });\n");
    }

    if (needsNullChecked) {
      builder.append("    }\n");
    }
  }

  static List<ListenerMethod> getListenerMethods(ListenerClass listener) {
    if (listener.method().length == 1) {
      return Arrays.asList(listener.method());
    }

    try {
      List<ListenerMethod> methods = new ArrayList<ListenerMethod>();
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

  private void emitReset(StringBuilder builder) {
    builder.append("  @Override public void reset(T target) {\n");
    if (parentInjector != null) {
      builder.append("    super.reset(target);\n\n");
    }
    for (ViewInjection injection : viewIdMap.values()) {
      for (ViewBinding viewBinding : injection.getViewBindings()) {
        builder.append("    target.").append(viewBinding.getName()).append(" = null;\n");
      }
    }
    for (CollectionBinding collectionBinding : collectionBindings.keySet()) {
      builder.append("    target.").append(collectionBinding.getName()).append(" = null;\n");
    }
    builder.append("  }\n");
  }

  static void emitCastIfNeeded(StringBuilder builder, String viewType) {
    emitCastIfNeeded(builder, VIEW_TYPE, viewType);
  }

  static void emitCastIfNeeded(StringBuilder builder, String sourceType, String destinationType) {
    // Only emit a cast if the source and destination type do not match.
    if (!sourceType.equals(destinationType)) {
      builder.append('(').append(destinationType).append(") ");
    }
  }

  static void emitHumanDescription(StringBuilder builder, List<Binding> bindings) {
    switch (bindings.size()) {
      case 1:
        builder.append(bindings.get(0).getDescription());
        break;
      case 2:
        builder.append(bindings.get(0).getDescription())
            .append(" and ")
            .append(bindings.get(1).getDescription());
        break;
      default:
        for (int i = 0, count = bindings.size(); i < count; i++) {
          Binding requiredField = bindings.get(i);
          if (i != 0) {
            builder.append(", ");
          }
          if (i == count - 1) {
            builder.append("and ");
          }
          builder.append(requiredField.getDescription());
        }
        break;
    }
  }
}
