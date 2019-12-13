package pt.pak3nuh.util.logviewer.view

import com.google.gson.JsonObject
import javafx.scene.Parent
import javafx.scene.control.SelectionMode
import javafx.scene.control.TextInputDialog
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.VBox
import pt.pak3nuh.util.logviewer.data.LogItem
import pt.pak3nuh.util.logviewer.file.FixedFileStructure
import pt.pak3nuh.util.logviewer.file.JsonFileStructure
import pt.pak3nuh.util.logviewer.file.json.jsonParser
import pt.pak3nuh.util.logviewer.view.control.ColumnDefinition
import pt.pak3nuh.util.logviewer.view.control.ItemFactory
import pt.pak3nuh.util.logviewer.view.control.Settings
import tornadofx.*
import java.io.File

// todo change to a modal dialog for better interface
class SettingsFragment(private val file: File, settings: Settings): Fragment("Settings") {

    private val includedColumns = observableListOf<Column>()
    private val availableColumns = observableListOf<Column>()
    private val builder = SettingsBuilder()
    var result: Settings? = null

    init {
        builder.separator = settings.separator
    }

    override val root: Parent = VBox().apply {
        textfield(file.absoluteFile.path)
        hbox {
            val toggle = ToggleGroup()
            radiobutton(text = "Separator char", group = toggle).action {
                builder.separator = TextInputDialog(builder.separator).showAndWait()
                        .map { if (it.isNullOrEmpty()) builder.separator else it.substring(0, 1) }
                        .orElse(builder.separator)
                builder.itemFactory = {
                    LogItem(it.split(builder.separator), it)
                }
                val elements = FixedFileStructure(file, builder.separator)
                        .readStructure()
                        .mapIndexed { idx, field ->
                            val definition = ColumnDefinition(field.name) {
                                (it.message as List<String>)[idx]
                            }
                            Column(field.name, definition)
                        }
                includedColumns.clear()
                availableColumns.clear()
                availableColumns.addAll(elements)
            }
            radiobutton(text = "Json", group = toggle).action {
                builder.itemFactory = { LogItem(jsonParser.fromJson(it, JsonObject::class.java), it) }
                val elements = JsonFileStructure(file)
                        .readStructure()
                        .map { field ->
                            val definition = ColumnDefinition(field.name) { logItem ->
                                (logItem.message as JsonObject)[field.name].toString()
                            }
                            Column(field.name, definition)
                        }
                includedColumns.clear()
                availableColumns.clear()
                availableColumns.addAll(elements)
            }
        }
        hbox {
            val toAdd = listview(includedColumns) {
                selectionModel.selectionMode = SelectionMode.SINGLE
            }
            val buttons = vbox()
            val all = listview(availableColumns) {
                selectionModel.selectionMode = SelectionMode.SINGLE
            }
            buttons.add(button("<<") { action { addColumn(all.selectedItem) } })
            buttons.add(button(">>") { action { removeColumn(toAdd.selectedItem) } })
        }
        hbox {
            button("OK") {
                action {
                    result = produceSettings()
                    close()
                }
            }
            button("Cancel") { action { close() } }
        }
    }

    private fun produceSettings(): Settings = builder.build(includedColumns.map(Column::definition))

    private fun addColumn(selectedItem: Column?) {
        if (selectedItem != null) {
            availableColumns.remove(selectedItem)
            includedColumns.add(selectedItem)
        }
    }

    private fun removeColumn(selectedItem: Column?) {
        if (selectedItem != null) {
            availableColumns.add(selectedItem)
            includedColumns.remove(selectedItem)
        }
    }
}

private class Column(val name: String, val definition: ColumnDefinition) {
    override fun toString(): String {
        return name
    }
}

private class SettingsBuilder {

    lateinit var separator: String
    lateinit var itemFactory: ItemFactory

    fun build(columns: List<ColumnDefinition>): Settings {
        return Settings(separator, columns, itemFactory)
    }

}
