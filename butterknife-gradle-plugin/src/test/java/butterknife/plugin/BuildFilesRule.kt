package butterknife.plugin

import com.google.common.truth.Truth.assertThat
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.File
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

class BuildFilesRule(private val root: File) : TestRule {

    @Target(FUNCTION)
    @Retention(RUNTIME)
    annotation class KotlinTest

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                val settingsFile = File(root, "settings.gradle")
                val hasSettingsFile = settingsFile.exists()
                if (!hasSettingsFile) settingsFile.writeText("")
                val buildFile = File(root, "build.gradle")
                val hasBuildFile = buildFile.exists()
                if (hasBuildFile) {
                    assertThat(buildFile.readText())
                } else {
                    val buildFileTemplate = File(root, "../../build.gradle").readText()
                    buildFile.writeText(buildFileTemplate)
                    if (description.getAnnotation(KotlinTest::class.java) != null) {
                        buildFile.appendText("""

                          butterKnife {
                            generateKotlin = true
                          }
                        """.trimIndent())
                    }
                }

                val manifestFile = File(root, "src/main/AndroidManifest.xml")
                val hasManifestFile = manifestFile.exists()
                if (!hasManifestFile) {
                    val manifestFileTemplate = File(root, "../../AndroidManifest.xml").readText()
                    manifestFile.writeText(manifestFileTemplate)
                }

                try {
                    base.evaluate()
                } finally {
                    if (!hasSettingsFile) settingsFile.delete()
                    if (!hasBuildFile) buildFile.delete()
                    if (!hasManifestFile) manifestFile.delete()
                }
            }
        }
    }
}