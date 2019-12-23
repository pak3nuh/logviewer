package pt.pak3nuh.util.logviewer.view.control

import pt.pak3nuh.util.logviewer.data.LogItem

typealias ItemFactory = (String) -> LogItem

class Settings(val separator: String, val columns: List<ColumnDefinition>, val itemFactory: ItemFactory) {

    var columnWidth: Double = 200.0

    companion object {
        fun createDefault(): Settings {
            return Settings(";", emptyList()) {
                LogItem(it, it)
            }
        }
    }
}

data class ColumnDefinition(
        val name: String,
        val getter: (LogItem) -> String
)
