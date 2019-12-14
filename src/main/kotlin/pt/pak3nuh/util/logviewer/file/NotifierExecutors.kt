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

    private val pollQueue: Queue<PollData> = ConcurrentLinkedDeque()
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

    fun enqueue(pollStrategy: PathPoll, consumer: (Sequence<Path>) -> Unit): AutoCloseable {
        val data = PollData(pollStrategy, consumer)
        pollQueue.offer(data)
        return AutoCloseable {
            data.expired = true
        }
    }

}

private data class PollData(val pollPathPoll: PathPoll, val consumer: (Sequence<Path>) -> Unit, var expired: Boolean = false)

private const val REFRESH_TIME_MS = 1_000L
private val logger = Logger.createLogger<PollThread>()

private class PollThread(private val queue: Queue<PollData>) : Thread("file-poll-${threadCounter.getAndIncrement()}") {

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
        val data: PollData? = queue.poll()
        logger.trace("Polling data %s", data)
        when {
            data == null -> sleep(REFRESH_TIME_MS)
            data.expired -> data.pollPathPoll.close()
            else -> {
                val size = queue.size + 1
                val timeout = REFRESH_TIME_MS / size
                val poll = data.pollPathPoll.poll(timeout, TimeUnit.MILLISECONDS)
                logger.trace("Notifying consumer with poll data")
                data.consumer(poll)
                queue.offer(data)
            }
        }
    }

    private companion object {
        val threadCounter = AtomicInteger(0)
    }
}

