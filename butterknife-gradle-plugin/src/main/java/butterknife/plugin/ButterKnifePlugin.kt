package butterknife.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import java.io.File
import kotlin.reflect.KClass

class ButterKnifePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.plugins.all {
      when (it) {
        is LibraryPlugin -> applyPlugin(project.extensions[LibraryExtension::class].libraryVariants)
        is AppPlugin -> applyPlugin(project.extensions[AppExtension::class].applicationVariants)
      }
    }
  }

  private fun applyPlugin(variants: DomainObjectSet<out BaseVariant>) {
    variants.all { variant ->
      variant.outputs.forEach { output ->
        val processResources = output.processResources
        // TODO proper task registered as source-generating?
        processResources.doLast {
          val pathToR = processResources.packageForR.replace('.', File.separatorChar)
          val rFile = processResources.sourceOutputDir.resolve(pathToR).resolve("R.java")

          FinalRClassBuilder.brewJava(rFile, processResources.sourceOutputDir,
              processResources.packageForR, "R2")
        }
      }
    }
  }

  private operator fun <T : Any> ExtensionContainer.get(type: KClass<T>): T {
    return getByType(type.java)!!
  }
}
