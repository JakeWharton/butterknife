package butterknife.internal;

import javax.lang.model.element.Element;

class InjectableListenerException extends Exception {
  final Element element;

  InjectableListenerException(Element element, String format, Object... args) {
    super(String.format(format, args));
    this.element = element;
  }
}
