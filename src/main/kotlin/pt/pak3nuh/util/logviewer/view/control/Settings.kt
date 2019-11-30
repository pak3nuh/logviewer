package pt.pak3nuh.util.logviewer.view.control

import pt.pak3nuh.util.logviewer.data.LogItem

class Settings(val separator: String, vararg columns: ColumnDefinition) {

    val columns: List<ColumnDefinition> = columns.asList()

    companion object {
        fun createDefault(): Settings {
            val starterDefinition = ColumnDefinition("Message") { it.message }
            return Settings(";", starterDefinition)
        }
    }
}

data class ColumnDefinition(
        val name: String,
        val getter: (LogItem) -> String
)