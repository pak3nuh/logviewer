package pt.pak3nuh.util.logviewer.file

import java.nio.file.Files
import java.nio.file.Path

private typealias LinesHandler = (Sequence<String>) -> Unit

class FileChangeNotifier(private val file: Path) : AutoCloseable {

    private val handlers = ArrayList<LinesHandler>()

    init {
        require(Files.exists(file)) { "File must exist" }
    }

    fun onNewLines(handler: LinesHandler) {
        handlers.add(handler)
    }

    fun start() {
        TODO("not implemented")
    }

    override fun close() {
        TODO("not implemented")
    }
}