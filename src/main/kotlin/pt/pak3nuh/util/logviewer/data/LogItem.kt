package pt.pak3nuh.util.logviewer.data

import java.util.concurrent.atomic.AtomicLong

class LogItem(val message: String) {

    val id = counter.getAndIncrement()

    override fun toString(): String {
        return message
    }

    private companion object {
        val counter = AtomicLong(0)
    }
}