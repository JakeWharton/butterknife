package sample.r2;

public class R2UsageOutsideAnnotations {

  int array = sample.r2.R2.array.res;

  public void foo(int color) {}

  public void bar() {
    foo(R2.color.res);
  }
}
