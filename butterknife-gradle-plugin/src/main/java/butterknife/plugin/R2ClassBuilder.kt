package butterknife.plugin

import java.io.File
import java.util.Locale

// TODO https://youtrack.jetbrains.com/issue/KT-28933
internal fun String.capitalize(locale: Locale) = substring(0, 1).toUpperCase(locale) + substring(1)

/**
 * High level interface for an "R2" class generator.
 */
internal interface R2ClassBuilder {
  /**
   * Adds a new resource constant to render.
   *
   * @param type the [ResourceType].
   * @param fieldName the resource field name.
   * @param fieldValue the constant resource field value.
   */
  fun addResourceConstant(type: ResourceType, fieldName: String, fieldValue: String)

  /**
   * Writes the created class to the given output directory. Package structure will be created as
   * needed.
   *
   * @property packageName the package name to write the RClass too
   * @property className the simple class name to use for the RClass.
   * @param outputDir the output directory.
   */
  fun write(
      packageName: String,
      className: String,
      outputDir: File)

  companion object {
    /** The androidx annotations package. */
    const val ANNOTATION_PACKAGE = "androidx.annotation"

    /** Supported resource types. */
    enum class ResourceType(val renderString: String) {
      ANIM("anim"),
      ARRAY("array"),
      ATTR("attr"),
      BOOL("bool"),
      COLOR("color"),
      DIMEN("dimen"),
      DRAWABLE("drawable"),
      ID("id"),
      INTEGER("integer"),
      LAYOUT("layout"),
      MENU("menu"),
      PLURALS("plurals"),
      STRING("string"),
      STYLE("style"),
      STYLEABLE("styleable");

      companion object {
        /** Mapping of all the renderStrings to [ResourceType] for lookups. */
        val RENDER_MAPPING = values().associate { it.renderString to it }
      }
    }
  }
}