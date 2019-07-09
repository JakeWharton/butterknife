package butterknife.plugin

import com.google.common.truth.Truth.assertAbout
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory.javaSource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class FinalRClassBuilderTest {
  @Rule @JvmField val tempFolder = TemporaryFolder()

  @Test fun brewJava() {
    val packageName = "com.butterknife.example"
    val rFile = tempFolder.newFile("R.txt").also {
      it.writeText(javaClass.getResource("/fixtures/R.txt").readText())
    }

    val outputDir = tempFolder.newFolder()
    brewJava(rFile, outputDir, packageName, "R2")

    val actual = outputDir.resolve("com/butterknife/example/R2.java").readText()
    val expected = javaClass.getResource("/fixtures/R2.java").readText()

    assertEquals(expected.trim(), actual.trim())

    val actualJava = JavaFileObjects.forSourceString("$packageName.R2", actual)
    assertAbout(javaSource()).that(actualJava).compilesWithoutError()
  }
}
