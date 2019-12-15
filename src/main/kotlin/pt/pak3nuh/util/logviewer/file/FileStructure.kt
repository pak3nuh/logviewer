package pt.pak3nuh.util.logviewer.file

import pt.pak3nuh.util.logviewer.file.json.JsonMap
import pt.pak3nuh.util.logviewer.file.json.JsonParsing
import java.io.File

abstract class FileStructure(
        private val file: File
) {
    init {
        require(file.exists())
    }

    fun readStructure(): List<FileField> {
        val line = getFirstLine()
        return readStructure(line)
    }

    protected abstract fun readStructure(line: String): List<FileField>

    private fun getFirstLine(): String {
        return file.useLines {
            it.first()
        }
    }
}

class JsonFileStructure(
        jsonFile: File
) : FileStructure(jsonFile) {

    override fun readStructure(line: String): List<FileField> {
        val json = JsonParsing.parse<JsonMap>(line)
        var counter = 0
        return json.keys.map { FileField(counter++, it) }
    }
}

class FixedFileStructure(
        file: File,
        private val separator: String
) : FileStructure(file) {

    override fun readStructure(line: String): List<FileField> {
        var counter = 0
        return line.split(separator).map { FileField(counter, "Field${counter++}") }
    }

}

data class FileField(val position: Int, val name: String)