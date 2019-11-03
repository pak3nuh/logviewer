package pt.pak3nuh.util.logviewer.util

import java.time.Clock
import java.time.LocalDateTime

/**
 * Basic console logger with [String.format] formatting.
 */
// no need for fancy logger libraries
class Logger(private val name: String?) {

    fun info(message: String, vararg params: Any) = log(LogLevel.INFO, message, params)
    fun trace(message: String, vararg params: Any) = log(LogLevel.TRACE, message, params)
    fun error(message: String, vararg params: Any) = log(LogLevel.ERROR, message, params)
    fun debug(message: String, vararg params: Any) = log(LogLevel.DEBUG, message, params)

    private fun log(msgLevel: LogLevel, message: String, params: Array<out Any>) {
        if (msgLevel.ordinal >= level.ordinal) {
            println("${LocalDateTime.now(Clock.systemUTC())} $name $level ${message.format(*params)}")
        }
    }

    companion object {
        var level = LogLevel.INFO

        inline fun <reified K> createLogger() = Logger(K::class.qualifiedName)

        inline fun <reified K> createLogger(suffix: String) = Logger("${K::class.qualifiedName}:$suffix")
    }
}

enum class LogLevel {
    TRACE, DEBUG, INFO, ERROR
}