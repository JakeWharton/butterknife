package butterknife.internal;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static butterknife.internal.InjectViewProcessor.VIEW_TYPE;

class TargetClass {
  private final Map<Integer, ViewId> viewIdMap = new LinkedHashMap<Integer, ViewId>();
  private final String classPackage;
  private final String className;
  private final String targetClass;
  private String parentInjector;

  TargetClass(String classPackage, String className, String targetClass) {
    this.classPackage = classPackage;
    this.className = className;
    this.targetClass = targetClass;
  }

  void addField(int id, String name, String type, boolean required) {
    getTargetView(id).fields.add(new FieldInjection(name, type, required));
  }

  boolean addMethod(int id, String name, String parameterType, boolean required) {
    ViewId targetView = getTargetView(id);
    if (targetView.method != null) {
      return false;
    }
    targetView.method = new MethodInjection(name, parameterType, required);
    return true;
  }

  void setParentInjector(String parentInjector) {
    this.parentInjector = parentInjector;
  }

  private ViewId getTargetView(int id) {
    ViewId viewId = viewIdMap.get(id);
    if (viewId == null) {
      viewId = new ViewId(id);
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
    builder.append("import butterknife.Views.Finder;\n\n");
    builder.append("public class ").append(className).append(" {\n");
    builder.append("  public static void inject(Finder finder, final ")
        .append(targetClass)
        .append(" target, Object source) {\n");
    if (parentInjector != null) {
      builder.append("    ")
          .append(parentInjector)
          .append(".inject(finder, target, source);\n\n");
    }
    builder.append("    View view;\n");
    for (Map.Entry<Integer, ViewId> entry : viewIdMap.entrySet()) {
      int id = entry.getKey();
      builder.append("    view = finder.findById(source, ").append(id).append(");\n");
      ViewId viewId = entry.getValue();
      for (FieldInjection fieldInjection : viewId.fields) {
        if (fieldInjection.required) {
          builder.append("    if (view == null) {\n")
              .append("      throw new IllegalStateException(\"Required view with id '")
              .append(id)
              .append("' for field '")
              .append(fieldInjection.name)
              .append("' was not found. If this field binding is optional add '@Optional'.\");\n")
              .append("    }\n");
        }
        builder.append("    target.")
            .append(fieldInjection.name)
            .append(" = ");

        // Only emit a cast if the type is not View.
        if (!VIEW_TYPE.equals(fieldInjection.type)) {
          builder.append("(")
            .append(fieldInjection.type)
            .append(") ");
        }

        builder.append("view;\n");
      }
      MethodInjection method = viewId.method;
      if (method != null) {
        if (method.required) {
          builder.append("    if (view == null) {\n")
              .append("      throw new IllegalStateException(\"Required view with id '")
              .append(id)
              .append("' for method '")
              .append(method.name)
              .append("' was not found. If this method binding is optional add '@Optional'.\");\n")
              .append("    }\n");
        } else {
          builder.append("    if (view != null) {\n  ");
        }
        builder.append("    view.setOnClickListener(new View.OnClickListener() {\n")
            .append("      @Override public void onClick(View view) {\n")
            .append("        target.").append(method.name).append("(");
        if (method.type != null) {
          // Only emit a cast if the type is not View.
          if (!VIEW_TYPE.equals(method.type)) {
            builder.append("(")
                .append(method.type)
                .append(") ");
          }
          builder.append("view");
        }
        builder.append(");\n")
            .append("      }\n")
            .append("    });\n");
        if (!method.required) {
          builder.append("    }\n");
        }
      }
    }
    builder.append("  }\n\n");
    builder.append("  public static void reset(").append(targetClass).append(" target) {\n");
    if (parentInjector != null) {
      builder.append("    ")
          .append(parentInjector)
          .append(".reset(target);\n\n");
    }
    for (ViewId viewId : viewIdMap.values()) {
      for (FieldInjection fieldInjection : viewId.fields) {
        builder.append("    target.").append(fieldInjection.name).append(" = null;\n");
      }
    }
    builder.append("  }\n");
    builder.append("}\n");
    return builder.toString();
  }

  static class ViewId {
    final int id;
    final Set<FieldInjection> fields = new LinkedHashSet<FieldInjection>();
    MethodInjection method;

    ViewId(int id) {
      this.id = id;
    }
  }

  static class FieldInjection {
    final String name;
    final String type;
    final boolean required;

    FieldInjection(String name, String type, boolean required) {
      this.name = name;
      this.type = type;
      this.required = required;
    }
  }

  static class MethodInjection {
    final String name;
    final String type;
    final boolean required;

    MethodInjection(String name, String type, boolean required) {
      this.name = name;
      this.type = type;
      this.required = required;
    }
  }
}
