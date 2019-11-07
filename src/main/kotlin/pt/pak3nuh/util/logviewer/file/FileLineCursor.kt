package pt.pak3nuh.util.logviewer.file

import java.io.BufferedReader
import java.io.FileReader
import java.nio.file.Path

class FileLineCursor(file: Path) : AutoCloseable {

    private val reader = BufferedReader(FileReader(file.toFile()))

    fun read(maxLines: Int = Int.MAX_VALUE): Sequence<String> {
        return generateSequence { reader.readLine() }
                .take(maxLines)
                .takeWhile { it: String? ->
                    it != null
                }.filter {
                    it.isNotEmpty()
                }
    }

    override fun close() {
        reader.close()
    }
}