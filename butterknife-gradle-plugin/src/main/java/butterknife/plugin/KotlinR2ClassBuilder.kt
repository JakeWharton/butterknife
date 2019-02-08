package butterknife.plugin

import butterknife.plugin.R2ClassBuilder.Companion.ResourceType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier.CONST
import com.squareup.kotlinpoet.KModifier.PUBLIC
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import java.util.Locale

/**
 * Generates a Kotlin object that contains all supported field names in an R file as `const` values.
 * Also enables adding support annotations to indicate the type of resource for every property.
 */
internal class KotlinR2ClassBuilder: R2ClassBuilder {

  private var resourceTypes = mutableMapOf<String, TypeSpec.Builder>()

  override fun write(
      packageName: String,
      className: String,
      outputDir: File) {
    val result = TypeSpec.objectBuilder(className)
        .addModifiers(PUBLIC)
    for (type in ResourceType.values()) {
      resourceTypes[type.renderString]?.let {
        result.addType(it.build())
      }
    }
    FileSpec.builder(packageName, className)
        .addComment("Generated code from Butter Knife gradle plugin. Do not modify!")
        .addType(result.build())
        .build()
        .writeTo(outputDir)
  }

  override fun addResourceConstant(type: ResourceType, fieldName: String, fieldValue: String) {
    val propertySpecBuilder = PropertySpec.builder(fieldName, INT)
        .addModifiers(PUBLIC, CONST)
        .initializer(fieldValue)

    propertySpecBuilder.addAnnotation(getSupportAnnotationClass(type))

    val resourceType =
        resourceTypes.getOrPut(type.renderString) {
          TypeSpec.objectBuilder(type.renderString).addModifiers(PUBLIC)
        }
    resourceType.addProperty(propertySpecBuilder.build())
  }

  private fun getSupportAnnotationClass(type: ResourceType): ClassName {
    return ClassName(R2ClassBuilder.ANNOTATION_PACKAGE, type.renderString.capitalize(Locale.US) + "Res")
  }
}
