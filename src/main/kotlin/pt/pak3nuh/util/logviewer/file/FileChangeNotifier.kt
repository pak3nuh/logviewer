package pt.pak3nuh.util.logviewer.file

import pt.pak3nuh.util.logviewer.util.Logger
import java.io.BufferedReader
import java.io.FileReader
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.util.concurrent.TimeUnit

internal typealias LinesHandler = (Sequence<String>) -> Unit

private val logger = Logger.createLogger<FileChangeNotifier>()

class FileChangeNotifier(private val file: Path) : AutoCloseable {

    private val handlers = ArrayList<LinesHandler>()
    private val filePollRunnable: BlockingPathPollRunnable
    private val cursor = FileLineCursor(file)

    init {
        require(Files.exists(file)) { "File must exist" }
        val folder: Path = file.parent
        val watchService: WatchService = FileSystems.getDefault().newWatchService()
        folder.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)
        filePollRunnable = BlockingPathPollRunnable(watchService, ::filesModified)
    }

    private fun filesModified(paths: Sequence<Path>) {
        logger.trace("File's folder modified")
        if (paths.any { it == file }) {
            logger.debug("File modified, notifying handlers")
            val lines = cursor.read(Int.MAX_VALUE)
            handlers.forEach { it(lines) }
        }
    }

    fun onNewLines(handler: LinesHandler) {
        handlers.add(handler)
    }

    fun start() {
        logger.debug("Starting notifier thread")
        val thread = Thread(filePollRunnable)
        thread.isDaemon = true
        thread.start()
    }

    override fun close() {
        logger.debug("Closing notifier")
        filePollRunnable.close()
        cursor.close()
    }
}

private class BlockingPathPollRunnable(private val watchService: WatchService, private val notify: (Sequence<Path>) -> Unit) :
        Runnable, AutoCloseable {

    @Volatile
    private var isClosed = false

    override fun run() {
        watchService.use {
            doPoll()
        }
    }

    private fun doPoll() {
        while (!isClosed) {
            val key: WatchKey? = watchService.poll(1, TimeUnit.SECONDS)
            if (key != null) {
                key.reset()
                val paths = key.pollEvents().asSequence().mapNotNull { it.context() as? Path }
                notify(paths)
            }
        }
    }

    override fun close() {
        isClosed = true
    }
}

private class FileLineCursor(file: Path) : AutoCloseable {

    private val reader = BufferedReader(FileReader(file.toFile()))

    fun read(maxLines: Int): Sequence<String> {
        return generateSequence { reader.readLine() }
                .take(maxLines)
                .takeWhile { it: String? ->
                    it != null
                }
    }

    override fun close() {
        reader.close()
    }
}