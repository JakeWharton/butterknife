package butterknife;

import java.lang.reflect.Field;

import static butterknife.ButterKnife.trySet;

final class FieldUnbinder implements Unbinder {
  private final Object target;
  private final Field field;

  FieldUnbinder(Object target, Field field) {
    this.target = target;
    this.field = field;
  }

  @Override public void unbind() {
    trySet(field, target, null);
  }
}
