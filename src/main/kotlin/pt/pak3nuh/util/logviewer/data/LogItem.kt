package pt.pak3nuh.util.logviewer.data

import java.util.concurrent.atomic.AtomicLong

/**
 * @param message the specialized representation for the message
 * @param asString the message as a string
 */
class LogItem(val message: Any, val asString: String) {

    val id = counter.getAndIncrement()

    override fun toString(): String {
        return asString
    }

    private companion object {
        val counter = AtomicLong(0)
    }
}