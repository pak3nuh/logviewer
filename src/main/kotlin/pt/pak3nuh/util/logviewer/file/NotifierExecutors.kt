package pt.pak3nuh.util.logviewer.file

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object NotifierExecutors {

    /**
     * Maximum number of files handled by each thread.
     * Once the number of files passes the threshold a new thread is created and the files are split evenly between
     * all available threads.
     */
    var filesPerThread: Int = 10
        set(value) {
            field = value
            rebalanceIfNeeded()
        }

    private val pollList = mutableListOf<PathPoll>()
    private var numThreads = 1
    private var executor = Executors.newFixedThreadPool(numThreads, PollThreadFactory(pollList))

    fun enqueue(runnable: PathPoll): AutoCloseable {
        pollList.add(runnable)
        rebalanceIfNeeded()
        return AutoCloseable {
            pollList.remove(runnable)
            runnable.close()
        }
    }

    fun shutdown() {
        executor.shutdown()
    }

    private fun rebalanceIfNeeded() {
        if (pollList.isEmpty())
            return

        val ratio = filesPerThread / pollList.size + 1
        if (ratio != numThreads) {
            numThreads = ratio
            executor.shutdown()
            executor = Executors.newFixedThreadPool(ratio, PollThreadFactory(pollList))
        }
    }

}

private const val REFRESH_TIME_MS = 1_000L

private class PollThreadFactory(private val list: List<PathPoll>) : ThreadFactory {

    private val counter = AtomicInteger()

    override fun newThread(r: Runnable) = PollThread(counter.getAndIncrement())

    private inner class PollThread(private val threadNumber: Int) : Thread("file-poll-$threadNumber") {
        override fun run() {
            val pollCopy = splitList()
            if (pollCopy.isEmpty()) {
                sleep(REFRESH_TIME_MS)
            } else {
                val timeout = REFRESH_TIME_MS / pollCopy.size
                pollCopy.forEach {
                    it.poll(timeout, TimeUnit.MILLISECONDS)
                }
            }

        }

        private fun splitList(): List<PathPoll> {
            // todo list may change size between sequence evaluation and process items twice or never
            // may not be a problem because it should processed on the next refresh
            // todo uneven numbers will miss the remainder
            val slice = list.size / threadNumber
            val offset = slice * threadNumber
            return list.asSequence().drop(offset).take(slice).toList()
        }
    }
}

