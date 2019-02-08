package butterknife.plugin

import butterknife.plugin.R2ClassBuilder.Companion.ResourceType.Companion.RENDER_MAPPING
import java.io.File

internal class ResourceSymbolListReader(private val builder: R2ClassBuilder) {

  fun readSymbolTable(symbolTable: File) {
    symbolTable.forEachLine { processLine(it) }
  }

  private fun processLine(line: String) {
    val values = line.split(' ')
    if (values.size < 4) {
      return
    }
    val javaType = values[0]
    if (javaType != "int") {
      return
    }
    val symbolType = values[1]
    val resourceType = RENDER_MAPPING[symbolType] ?: return
    val name = values[2]
    val value = values[3]
    builder.addResourceField(resourceType, name, value)
  }
}
