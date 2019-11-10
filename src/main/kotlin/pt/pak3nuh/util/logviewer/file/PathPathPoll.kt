package pt.pak3nuh.util.logviewer.file

import java.nio.file.Path
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.util.concurrent.TimeUnit

interface PathPoll : AutoCloseable {
    fun poll(timeout: Long, unit: TimeUnit): Sequence<Path>
}

class PathPollImpl(
        private val watchService: WatchService
) : PathPoll {

    override fun poll(timeout: Long, unit: TimeUnit): Sequence<Path> {
        val key: WatchKey? = watchService.poll(timeout, unit)
        return if (key != null) {
            val paths = key.pollEvents().asSequence().mapNotNull { it.context() as? Path }
            key.reset()
            paths
        } else {
            emptySequence()
        }
    }

    override fun close() {
        watchService.close()
    }
}