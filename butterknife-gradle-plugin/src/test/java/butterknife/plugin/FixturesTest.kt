package butterknife.plugin

import com.google.common.truth.Truth.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.io.File


@RunWith(Parameterized::class)
class FixturesTest(val fixtureRoot: File, val name: String) {
    @Suppress("unused") // Used by JUnit reflectively.
    @get:Rule val buildFilesRule = BuildFilesRule(fixtureRoot)

    @Test fun execute() {
        val androidHome = androidHome()
        File(fixtureRoot, "local.properties").writeText("sdk.dir=$androidHome\n")

        val butterKnifeVersion = System.getProperty("butterknife.version")!!

        val runner = GradleRunner.create()
                .withProjectDir(fixtureRoot)
                .withPluginClasspath()
                .withArguments("clean", "assembleDebug", "assembleRelease", "--stacktrace",
                        "-Pbutterknife.version=$butterKnifeVersion")

        if (File(fixtureRoot, "ignored.txt").exists()) {
            println("Skipping ignored test $name.")
            return
        }

        val expectedFailure = File(fixtureRoot, "failure.txt")
        if (expectedFailure.exists()) {
            val result = runner.buildAndFail()
            for (chunk in expectedFailure.readText().split("\n\n")) {
                assertThat(result.output).contains(chunk)
            }
        } else {
            val result = runner.build()
            assertThat(result.output).contains("BUILD SUCCESSFUL")
        }
    }

    companion object {
        @Suppress("unused") // Used by Parameterized JUnit runner reflectively.
        @Parameters(name = "{1}")
        @JvmStatic fun parameters() = File("src/test/fixtures").listFiles()
                .filter { it.isDirectory }
                .map { arrayOf(it, it.name) }
    }
}