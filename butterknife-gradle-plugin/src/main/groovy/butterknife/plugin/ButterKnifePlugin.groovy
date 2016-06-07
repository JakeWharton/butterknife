package butterknife.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.TestPlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.BaseVariantOutput
import org.gradle.api.Plugin
import org.gradle.api.Project

public class ButterKnifePlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    if (!(project.plugins.hasPlugin(LibraryPlugin) || project.plugins.hasPlugin(AppPlugin))) {
      throw new IllegalStateException('Butterknife plugin can only be applied to android projects')
    }

    def variants
    if (project.plugins.hasPlugin(LibraryPlugin)) {
      variants = project.android.libraryVariants
    } else {
      variants = project.android.applicationVariants
    }

    project.afterEvaluate {
      variants.all { BaseVariant variant ->
        variant.outputs.each { BaseVariantOutput output ->
          output.processResources.doLast {
            File rDir = new File(sourceOutputDir, packageForR.replaceAll('\\.', File.separator))
            File R = new File(rDir, 'R.java')
            FinalRClassBuilder.brewJava(R, sourceOutputDir, packageForR, 'R2')
          }
        }
      }
    }
  }
}
