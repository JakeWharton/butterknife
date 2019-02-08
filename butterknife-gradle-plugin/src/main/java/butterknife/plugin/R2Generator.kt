package butterknife.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class R2Generator : DefaultTask() {
  @get:OutputDirectory
  lateinit var outputDir: File

  @get:InputFiles
  lateinit var rFile: FileCollection

  @get:Input
  lateinit var packageName: String

  @get:Input
  lateinit var className: String

  @get:Input
  var generateKotlin: Boolean = false

  @Suppress("unused") // Invoked by Gradle.
  @TaskAction
  fun generate() {
    generateFile(rFile.singleFile, outputDir, packageName, className, generateKotlin)
  }
}

fun generateFile(
  rFile: File,
  outputDir: File,
  packageName: String,
  className: String,
  generateKotlin: Boolean
) {
  val classBuilder = if (generateKotlin) KotlinR2ClassBuilder() else JavaR2ClassBuilder()
  classBuilder
      .also { ResourceSymbolListReader(it).readSymbolTable(rFile) }
      .write(packageName, className, outputDir)
}
