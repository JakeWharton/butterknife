package butterknife.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

import butterknife.BindFunc;

final class FieldBeanBinding implements ResourceBinding{
//  enum Type {
//    TEXT("setText"),
//    IMAGE("setImage");
//
//    private final List<BeanMethod> methods;
//
//    Type(BeanMethod... methods) {
//      List<BeanMethod> methodList = new ArrayList<>(methods.length);
//      Collections.addAll(methodList, methods);
//      Collections.sort(methodList);
//      Collections.reverse(methodList);
//      this.methods = unmodifiableList(methodList);
//    }
//
//    Type(String methodName) {
//      methods = singletonList(new BeanMethod(null, methodName,  1));
//    }
//
//    BeanMethod methodForSdk(int sdk) {
//      for (BeanMethod method : methods) {
//        if (method.sdk <= sdk) {
//          return method;
//        }
//      }
//      throw new AssertionError();
//    }
//  }

  static final class BeanMethod implements Comparable<BeanMethod> {
    final ClassName typeName;
    final String name;

    final int sdk;
    BeanMethod(ClassName typeName, String name, int sdk) {
      this.typeName = typeName;
      this.name = name;
      this.sdk = sdk;

    }

    @Override public int compareTo(BeanMethod other) {
      return Integer.compare(sdk, other.sdk);
    }
  }

  private final Id id;
  private final String name;
  private final String filed;
  private final BindFunc func;
  FieldBeanBinding(Id id, String name, String filed, BindFunc func) {
    this.id = id;
    this.name = name;
    this.filed = filed;
    this.func=func;
  }

  @Override public Id id() {
    return id;
  }

  @Override public boolean requiresResources(int sdk) {
    return false;
  }

  @Override public CodeBlock render(int sdk) {
    //BeanMethod method = type.methodForSdk(sdk);
    return CodeBlock.of("target.$L.$L(bean.$L)", name, func.value(),filed);
  }
}
