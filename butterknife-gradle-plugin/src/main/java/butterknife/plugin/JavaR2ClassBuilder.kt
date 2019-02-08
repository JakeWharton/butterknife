package butterknife.plugin

import butterknife.plugin.R2ClassBuilder.Companion.ResourceType
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import java.io.File
import java.util.Locale
import javax.lang.model.element.Modifier.FINAL
import javax.lang.model.element.Modifier.PUBLIC
import javax.lang.model.element.Modifier.STATIC

/**
 * Generates a Java class that contains all supported field names in an R file as final values.
 * Also enables adding support annotations to indicate the type of resource for every field.
 */
internal class JavaR2ClassBuilder: R2ClassBuilder {

  private var resourceTypes = mutableMapOf<String, TypeSpec.Builder>()

  override fun write(
      packageName: String,
      className: String,
      outputDir: File) {
    val result = TypeSpec.classBuilder(className)
        .addModifiers(PUBLIC, FINAL)
    for (type in ResourceType.values()) {
      resourceTypes[type.renderString]?.let {
        result.addType(it.build())
      }
    }
    JavaFile.builder(packageName, result.build())
        .addFileComment("Generated code from Butter Knife gradle plugin. Do not modify!")
        .build()
        .writeTo(outputDir)
  }

  override fun addResourceField(type: ResourceType, fieldName: String, fieldValue: String) {
    val fieldSpecBuilder = FieldSpec.builder(Int::class.javaPrimitiveType, fieldName)
        .addModifiers(PUBLIC, STATIC, FINAL)
        .initializer(fieldValue)

    fieldSpecBuilder.addAnnotation(getSupportAnnotationClass(type))

    val resourceType =
        resourceTypes.getOrPut(type.renderString) {
          TypeSpec.classBuilder(type.renderString).addModifiers(PUBLIC, STATIC, FINAL)
        }
    resourceType.addField(fieldSpecBuilder.build())
  }

  private fun getSupportAnnotationClass(type: ResourceType): ClassName {
    return ClassName.get(R2ClassBuilder.ANNOTATION_PACKAGE, type.renderString.capitalize(Locale.US) + "Res")
  }

  // TODO https://youtrack.jetbrains.com/issue/KT-28933
  private fun String.capitalize(locale: Locale) = substring(0, 1).toUpperCase(locale) + substring(1)
}
