package pt.pak3nuh.util.logviewer.file

import java.nio.file.Path
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.util.concurrent.TimeUnit

interface PathPoll : AutoCloseable {
    fun poll(timeout: Long, unit: TimeUnit)
}

class PathPollImpl(
        private val watchService: WatchService,
        private val notify: (Sequence<Path>) -> Unit
) : PathPoll {

    override fun poll(timeout: Long, unit: TimeUnit) {
        val key: WatchKey? = watchService.poll(timeout, unit)
        if (key != null) {
            val paths = key.pollEvents().asSequence().mapNotNull { it.context() as? Path }
            key.reset()
            notify(paths)
        }
    }

    override fun close() {
        watchService.close()
    }
}