package butterknife.plugin

import com.google.common.collect.ImmutableSortedSet
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import java.util.*
import javax.lang.model.element.Modifier.*

/**
 * Generates a class that contains all supported field names in an R file as final values.
 * Also enables adding support annotations to indicate the type of resource for every field.
 */
class FinalRClassBuilder(
        private val packageName: String,
        private val className: String,
        private val useLegacyTypes: Boolean) {

    companion object {
        private const val ANNOTATION_PACKAGE = "androidx.annotation"
        private const val ANNOTATION_PACKAGE_LEGACY = "android.support.annotation"
        private val SUPPORTED_TYPES = ImmutableSortedSet.of("anim", "array", "attr", "bool", "color", "dimen", "drawable", "id", "integer", "layout", "menu", "plurals", "string", "style", "styleable")
    }

    private var resourceTypes: MutableMap<String, TypeSpec.Builder> = HashMap()

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

    fun addResourceField(type: String, fieldName: String, fieldValue: String) {
        if (!isSupported(type)) {
            return
        }
        val fieldSpecBuilder = FieldSpec.builder(Int::class.javaPrimitiveType, fieldName)
                .addModifiers(PUBLIC, STATIC, FINAL)
                .initializer(fieldValue)

        fieldSpecBuilder.addAnnotation(getSupportAnnotationClass(type))

        val resourceType =
                resourceTypes.getOrPut(type) { TypeSpec.classBuilder(type).addModifiers(PUBLIC, STATIC, FINAL) }
        resourceType.addField(fieldSpecBuilder.build())
    }

    private fun getSupportAnnotationClass(type: String): ClassName {
        val supportPackage = if (useLegacyTypes) ANNOTATION_PACKAGE_LEGACY else ANNOTATION_PACKAGE
        return ClassName.get(supportPackage, capitalize(type) + "Res")
    }

    private fun capitalize(word: String): String {
        return Character.toUpperCase(word[0]) + word.substring(1)
    }

    fun isSupported(type: String): Boolean {
        return SUPPORTED_TYPES.contains(type);
    }
}
