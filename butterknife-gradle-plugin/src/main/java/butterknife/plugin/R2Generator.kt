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
  var outputDir: File? = null

  @get:InputFiles
  var rFile: FileCollection? = null

  @get:Input
  var packageName: String? = null

  @get:Input
  var className: String? = null

  @Suppress("unused") // Invoked by Gradle.
  @TaskAction
  fun generate() {
    generateFile(rFile!!.singleFile, outputDir!!, packageName!!, className!!)
  }
}

internal fun generateFile(
  rFile: File,
  outputDir: File,
  packageName: String,
  className: String
) {
  JavaR2ClassBuilder()
      .also { ResourceSymbolListReader(it).readSymbolTable(rFile) }
      .write(packageName, className, outputDir)
}
