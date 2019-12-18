package butterknife.plugin

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import java.util.Locale
import javax.lang.model.element.Modifier.FINAL
import javax.lang.model.element.Modifier.PUBLIC
import javax.lang.model.element.Modifier.STATIC


private const val ANNOTATION_PACKAGE = "androidx.annotation"
internal val SUPPORTED_TYPES = setOf("anim", "array", "attr", "bool", "color", "dimen",
    "drawable", "id", "integer", "layout", "menu", "plurals", "string", "style", "styleable")

/**
 * Generates a class that contains all supported field names in an R file as final values.
 * Also enables adding support annotations to indicate the type of resource for every field.
 */
class FinalRClassBuilder(
  private val packageName: String,
  private val className: String
) {

  private var resourceTypes = mutableMapOf<String, TypeSpec.Builder>()

  fun build(): JavaFile {
    val result = TypeSpec.classBuilder(className)
        .addModifiers(PUBLIC, FINAL)
    for (type in SUPPORTED_TYPES) {
      resourceTypes.get(type)?.let {
        result.addType(it.build())
      }
    }
    return JavaFile.builder(packageName, result.build())
        .addFileComment("Generated code from Butter Knife gradle plugin. Do not modify!")
        .build()
  }

  fun addResourceField(type: String, fieldName: String, fieldInitializer: CodeBlock) {
    if (type !in SUPPORTED_TYPES) {
      return
    }
    val fieldSpecBuilder = FieldSpec.builder(Int::class.javaPrimitiveType, fieldName)
        .addModifiers(PUBLIC, STATIC, FINAL)
        .initializer(fieldInitializer)

    fieldSpecBuilder.addAnnotation(getSupportAnnotationClass(type))

    val resourceType =
        resourceTypes.getOrPut(type) {
          TypeSpec.classBuilder(type).addModifiers(PUBLIC, STATIC, FINAL)
        }
    resourceType.addField(fieldSpecBuilder.build())
  }

  private fun getSupportAnnotationClass(type: String): ClassName {
    return ClassName.get(ANNOTATION_PACKAGE, type.capitalize(Locale.US) + "Res")
  }

  // TODO https://youtrack.jetbrains.com/issue/KT-28933
  private fun String.capitalize(locale: Locale) = substring(0, 1).toUpperCase(locale) + substring(1)
}
