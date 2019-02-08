package butterknife.plugin

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class KotlinRClassBuilderTest {
  @Rule @JvmField val tempFolder = TemporaryFolder()

  @Test fun generateFile() {
    val packageName = "com.butterknife.example"
    val rFile = tempFolder.newFile("R.txt").also {
      it.writeText(javaClass.getResource("/fixtures/R.txt").readText())
    }

    val outputDir = tempFolder.newFolder()
    generateFile(rFile, outputDir, packageName, "R2", true)

    val actual = outputDir.resolve("com/butterknife/example/R2.kt").readText()
    val expected = javaClass.getResource("/fixtures/R2.kt").readText()

    assertEquals(expected.trim(), actual.trim())
  }
}
