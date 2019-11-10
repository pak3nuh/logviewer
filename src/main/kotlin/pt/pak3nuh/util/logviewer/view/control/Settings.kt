package pt.pak3nuh.util.logviewer.view.control

import pt.pak3nuh.util.logviewer.data.LogItem

class Settings {

    val columns: MutableList<ColumnDefinition> = arrayListOf()

    init {
        val starterDefinition = ColumnDefinition("Message") { it.message }
        columns.add(starterDefinition)
    }

}

data class ColumnDefinition(
        val name: String,
        val getter: (LogItem) -> String
)