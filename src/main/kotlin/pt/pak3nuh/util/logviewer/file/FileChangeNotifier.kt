package pt.pak3nuh.util.logviewer.file

import pt.pak3nuh.util.logviewer.util.Logger
import java.nio.file.*

internal typealias LinesHandler = (Sequence<String>) -> Unit

class FileChangeNotifier(private val file: Path) : AutoCloseable {

    private val logger = Logger.createLogger<FileChangeNotifier>(file.fileName.toString())

    private val handlers = ArrayList<LinesHandler>()
    private val closeHandle: AutoCloseable
    private val cursor = FileLineCursor(file)
    private var state = NotifierState.STARTED

    init {
        require(Files.exists(file)) { "File must exist" }
        val folder: Path = file.parent
        val watchService: WatchService = FileSystems.getDefault().newWatchService()
        folder.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)
        closeHandle = NotifierExecutors.enqueue(PathPollImpl(watchService), ::filesModified)
    }

    private fun filesModified(paths: Sequence<Path>) {
        if (state === NotifierState.PAUSED)
            return

        logger.trace("File's folder modified")
        if (paths.any { it.fileName == file.fileName }) {
            logger.debug("File modified, notifying handlers")
            readLinesAndNotifyHandlers()
        }
    }

    private fun readLinesAndNotifyHandlers() {
        logger.debug("Reading lines")
        val lines = cursor.read()
        logger.debug("Notifying handlers")
        handlers.forEach { it(lines) }
    }

    fun onNewLines(handler: LinesHandler) {
        handlers.add(handler)
    }

    fun start() {
        logger.debug("Starting file reader")
        readLinesAndNotifyHandlers()
        state = NotifierState.STARTED
    }

    fun pause() {
        logger.debug("Pausing file reader")
        state = NotifierState.PAUSED
    }

    override fun close() {
        logger.debug("Closing notifier")
        closeHandle.close()
        cursor.close()
    }
}

enum class NotifierState {
    STARTED, PAUSED
}
