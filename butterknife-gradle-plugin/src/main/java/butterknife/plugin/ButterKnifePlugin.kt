package butterknife.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.FeatureExtension
import com.android.build.gradle.FeaturePlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import java.io.File
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

  private fun configureR2Generation(project: Project, variants: DomainObjectSet<out BaseVariant>) {
    variants.all { variant ->
      val outputDir = project.buildDir.resolve(
          "generated/source/r2/${variant.dirName}")

      val task = project.tasks.create("generate${variant.name.capitalize()}R2")
      task.outputs.dir(outputDir)
      variant.registerJavaGeneratingTask(task, outputDir)

      val once = AtomicBoolean()
      variant.outputs.all { output ->
        val processResources = output.processResources
        task.dependsOn(processResources)

        // Though there might be multiple outputs, their R files are all the same. Thus, we only
        // need to configure the task once with the R.java input and action.
        if (once.compareAndSet(false, true)) {
          val rPackage = processResources.packageForR
          val pathToR = rPackage.replace('.', File.separatorChar)
          val rFile = processResources.sourceOutputDir.resolve(pathToR).resolve("R.java")

          task.apply {
            inputs.file(rFile)

            doLast {
              FinalRClassBuilder.brewJava(rFile, outputDir, rPackage, "R2")
            }
          }
        }
      }
    }
  }

  private operator fun <T : Any> ExtensionContainer.get(type: KClass<T>): T {
    return getByType(type.java)!!
  }
}
