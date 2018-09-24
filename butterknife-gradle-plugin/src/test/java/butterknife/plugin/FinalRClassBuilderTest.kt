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
class FinalRClassBuilderTest(val rFile: String, val r2File: String, val useLegacyTypes: Boolean) {
  @Rule @JvmField val tempFolder = TemporaryFolder()

  @Test fun brewJava() {
    val packageName = "com.butterknife.example"

    val rFile = tempFolder.newFile("R.java").also {
      it.writeText(javaClass.getResource("/fixtures/$rFile.java").readText())
    }

    val outputDir = tempFolder.newFolder()
    FinalRClassBuilder.brewJava(rFile, outputDir, packageName, "R2", useLegacyTypes)

    val actual = outputDir.resolve("com/butterknife/example/R2.java").readText()
    var expected = javaClass.getResource("/fixtures/$r2File.java").readText()
    if (useLegacyTypes) {
      expected = expected.replace("import androidx.", "import android.support.")
    }

    assertEquals(expected.trim(), actual.trim())

    val actualJava = JavaFileObjects.forSourceString("$packageName.R2", actual)
    assertAbout(javaSource()).that(actualJava).compilesWithoutError()
  }

  companion object {
    @JvmStatic @Parameters fun data() = listOf(
        arrayOf("R", "R2", false),
        arrayOf("RFinal", "R2", false),
        arrayOf("R", "R2", true),
        arrayOf("RFinal", "R2", true)
    )
  }
}
