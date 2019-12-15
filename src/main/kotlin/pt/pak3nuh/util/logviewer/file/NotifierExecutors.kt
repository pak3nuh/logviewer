package pt.pak3nuh.util.logviewer.file

import pt.pak3nuh.util.logviewer.util.Logger
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object NotifierExecutors {

    /**
     * Number of threads available to process the polling of the files
     */
    var numberOfThreads: Int = 1
        set(value) {
            field = value
            rebalance(numberOfThreads - threadQueue.size)
        }

    private val pollQueue: Queue<PathMonitor> = ConcurrentLinkedDeque()
    private val threadQueue: MutableList<PollThread> = arrayListOf(createPollThread())

    private fun createPollThread(): PollThread {
        val pollThread = PollThread(pollQueue)
        pollThread.start()
        return pollThread
    }

    @Synchronized
    private fun rebalance(threadsToAdd: Int) {
        if (threadsToAdd > 0) {
            repeat(threadsToAdd) {
                threadQueue.add(createPollThread())
            }
        } else {
            var counter = 0
            threadQueue.removeIf {
                if (counter-- > threadsToAdd) {
                    it.closed = true
                    true
                } else
                    false
            }
        }
    }

    fun enqueue(pollStrategy: PollStrategy, consumer: (Sequence<Path>) -> Unit): AutoCloseable {
        val data = PathMonitor(pollStrategy, consumer)
        pollQueue.offer(data)
        return AutoCloseable {
            data.expired = true
        }
    }

}

private class PathMonitor(
        private val pollStrategy: PollStrategy,
        private val consumer: (Sequence<Path>) -> Unit,
        var expired: Boolean = false
) {
    fun pollAndConsume(timeout: Long, unit: TimeUnit) {
        consumer(pollStrategy.poll(timeout, unit))
    }
    fun close() = pollStrategy.close()
}

private class PollThread(private val queue: Queue<PathMonitor>) : Thread("file-poll-${threadCounter.getAndIncrement()}") {

    init {
        isDaemon = true
    }

    @Volatile
    var closed: Boolean = false

    override fun run() {
        while (!closed) {
            doPoll()
        }
    }

    private fun doPoll() {
        val data: PathMonitor? = queue.poll()
        when {
            data == null -> {
                logger.trace("No paths being monitored")
                sleep(REFRESH_TIME_MS)
            }
            data.expired -> {
                logger.trace("Data %s is expired, closing", data)
                data.close()
            }
            else -> {
                val size = queue.size + 1
                val timeout = REFRESH_TIME_MS / size
                logger.trace("Polling data %s", data)
                data.pollAndConsume(timeout, TimeUnit.MILLISECONDS)
                queue.offer(data)
            }
        }
    }

    private companion object {
        private val threadCounter = AtomicInteger(0)
        private const val REFRESH_TIME_MS = 1_000L
        private val logger = Logger.createLogger<PollThread>()
    }
}

