package sample.r2;

public class R2UsageInAnnotations {

  @BindTest(sample.r2.R2.string.res) String test;

  @BindTest(R2.id.res) public void foo() { }
}
