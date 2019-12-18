package butterknife.plugin

import com.squareup.javapoet.CodeBlock
import java.io.File

internal class ResourceSymbolListReader(private val builder: FinalRClassBuilder) {
  private var idValue = 0

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
    if (symbolType !in SUPPORTED_TYPES) {
      return
    }
    val name = values[2]
    val value = CodeBlock.of("\$L", ++idValue)
    builder.addResourceField(symbolType, name, value)
  }
}
