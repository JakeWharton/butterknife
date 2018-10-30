package butterknife.plugin

import com.google.common.truth.Truth.assertAbout
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory.javaSource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class FinalRClassBuilderTest(private val useLegacyTypes: Boolean) {
  @Rule @JvmField val tempFolder = TemporaryFolder()

  @Test fun brewJava() {
    val packageName = "com.butterknife.example"
    val rFile = tempFolder.newFile("R.txt").also {
      it.writeText(javaClass.getResource("/fixtures/R.txt").readText())
    }

    val outputDir = tempFolder.newFolder()
    brewJava(rFile, outputDir, packageName, "R2", useLegacyTypes)

    val actual = outputDir.resolve("com/butterknife/example/R2.java").readText()
    var expected = javaClass.getResource("/fixtures/R2.java").readText()
    if (useLegacyTypes) {
      expected = expected.replace("import androidx.", "import android.support.")
    }

    assertEquals(expected.trim(), actual.trim())

    val actualJava = JavaFileObjects.forSourceString("$packageName.R2", actual)
    assertAbout(javaSource()).that(actualJava).compilesWithoutError()
  }

  companion object {
    @JvmStatic @Parameters(name="useLegacyTypes={0}") fun data() = listOf(false, true)
  }
}
