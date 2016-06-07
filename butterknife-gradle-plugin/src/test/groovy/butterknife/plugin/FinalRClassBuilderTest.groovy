package butterknife.plugin

import com.google.testing.compile.JavaFileObjects
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

import javax.tools.JavaFileObject

import static com.google.common.truth.Truth.assertAbout
import static com.google.common.truth.Truth.assertThat
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource

@RunWith(Parameterized)
class FinalRClassBuilderTest {

  @Parameters
  static Collection<Object[]> data() {
    def cases = [
        ['R', 'R2'],
        ['RFinal', 'R2']
    ]
    return cases.collect { it as Object[] }
  }

  private String RFile
  private String R2File

  FinalRClassBuilderTest(String RFile, String R2File) {
    this.RFile = RFile
    this.R2File = R2File
  }

  @Test
  public void brewJava() throws Exception {
    String packageName = 'com.butterknife.example'

    File R = File.createTempFile('test', 'R')
    R.text = getClass().getResource("/fixtures/${RFile}.java").text

    File R2D2 = File.createTempDir()
    FinalRClassBuilder.brewJava(R, R2D2, packageName, 'R2')

    File R2 = new File(R2D2, "com/butterknife/example/R2.java")
    assertThat(R2.text.trim()).isEqualTo(getClass().getResource("/fixtures/${R2File}.java").text.trim())

    JavaFileObject expected = JavaFileObjects.forSourceString("com.butterknife.example.R2", R2.text)
    assertAbout(javaSource()).that(expected).compilesWithoutError()
  }
}
