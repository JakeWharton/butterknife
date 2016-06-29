package sample.r2;

public class R2UsageWithSuppression {

  @SuppressWarnings("InvalidR2Usage")
  int bool = sample.r2.R2.bool.res;

  public void foo(int attr) {}

  @SuppressWarnings("InvalidR2Usage")
  public void bar() {
    foo(R2.attr.res);
  }
}
