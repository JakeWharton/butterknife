package butterknife.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.FeatureExtension
import com.android.build.gradle.FeaturePlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.res.GenerateLibraryRFileTask
import com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask
import groovy.util.XmlSlurper
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass

class ButterKnifePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.plugins.all {
      when (it) {
        is FeaturePlugin -> {
          project.extensions[FeatureExtension::class].run {
            configureR2Generation(project, featureVariants)
            configureR2Generation(project, libraryVariants)
          }
        }
        is LibraryPlugin -> {
          project.extensions[LibraryExtension::class].run {
            configureR2Generation(project, libraryVariants)
          }
        }
        is AppPlugin -> {
          project.extensions[AppExtension::class].run {
            configureR2Generation(project, applicationVariants)
          }
        }
      }
    }
  }

  // Parse the variant's main manifest file in order to get the package id which is used to create
  // R.java in the right place.
  private fun getPackageName(variant : BaseVariant) : String {
    val slurper = XmlSlurper(false, false)
    val list = variant.sourceSets.map { it.manifestFile }

    // According to the documentation, the earlier files in the list are meant to be overridden by the later ones.
    // So the first file in the sourceSets list should be main.
    val result = slurper.parse(list[0])
    return result.getProperty("@package").toString()
  }

  private fun configureR2Generation(project: Project, variants: DomainObjectSet<out BaseVariant>) {
    variants.all { variant ->
      val outputDir = project.buildDir.resolve(
          "generated/source/r2/${variant.dirName}")

      val rPackage = getPackageName(variant)
      val once = AtomicBoolean()
      variant.outputs.all { output ->
        val processResources = output.processResourcesProvider

        // Though there might be multiple outputs, their R files are all the same. Thus, we only
        // need to configure the task once with the R.java input and action.
        if (once.compareAndSet(false, true)) {
          project.tasks.create("generate${variant.name.capitalize()}R2", R2Generator::class.java) {
            val processResourcesTask = processResources.get()
            it.outputDir = outputDir
            // TODO: switch to better API once exists in AGP (https://issuetracker.google.com/118668005)
            it.rFile = project.files(
                    when (processResourcesTask) {
                      is GenerateLibraryRFileTask -> processResourcesTask.textSymbolOutputFile
                      is LinkApplicationAndroidResourcesTask -> processResourcesTask.textSymbolOutputFile
                      else -> throw RuntimeException(
                              "Minimum supported Android Gradle Plugin is 3.3.0")
                    })
                    .builtBy(processResourcesTask)
            it.packageName = rPackage
            it.className = "R2"
            // TODO: switch to as task provider when AGP supports it: (https://issuetracker.google.com/issues/123655659)
            variant.registerJavaGeneratingTask(it, outputDir)
          }
        }
      }
    }
  }

  private operator fun <T : Any> ExtensionContainer.get(type: KClass<T>): T {
    return getByType(type.java)
  }
}
