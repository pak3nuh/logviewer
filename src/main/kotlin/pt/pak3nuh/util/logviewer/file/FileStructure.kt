package pt.pak3nuh.util.logviewer.file

import com.google.gson.Gson
import com.google.gson.JsonObject
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
        val json = gson.fromJson(line, JsonObject::class.java)
        var counter = 0
        return json.keySet().map { FileField(counter++, it) }
    }

    private companion object {
        val gson = Gson()
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