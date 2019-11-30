package pt.pak3nuh.util.logviewer.view

import javafx.scene.Parent
import javafx.scene.control.SelectionMode
import javafx.scene.control.TextInputDialog
import javafx.scene.control.ToggleGroup
import pt.pak3nuh.util.logviewer.file.FileField
import pt.pak3nuh.util.logviewer.file.FileStructure
import pt.pak3nuh.util.logviewer.file.FixedFileStructure
import pt.pak3nuh.util.logviewer.file.JsonFileStructure
import pt.pak3nuh.util.logviewer.view.control.Settings
import tornadofx.*
import java.io.File

// todo change to a modal dialog for better interface
class SettingsFragment(private val file: File, settings: Settings) : Fragment("Settings") {

    private val includedColumns = observableListOf<String>()
    private val availableColumns = observableListOf<String>()
    private var fieldList = listOf<FileField>()
    private var separator = settings.separator
    var result: Settings? = null

    override val root: Parent = vbox {
        textfield(file.absoluteFile.path)
        hbox {
            val toggle = ToggleGroup()
            radiobutton(text = "Separator char", group = toggle).action {
                separator = TextInputDialog(separator).showAndWait()
                        .map { if (it.isNullOrEmpty()) separator else it.substring(0, 1) }
                        .orElse(separator)
                parse(FixedFileStructure(file, separator))
            }
            radiobutton(text = "Json", group = toggle).action {
                parse(JsonFileStructure(file))
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

    private fun produceSettings(): Settings {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun addColumn(selectedItem: String?) {
        if (selectedItem != null) {
            availableColumns.remove(selectedItem)
            includedColumns.add(selectedItem)
        }
    }

    private fun removeColumn(selectedItem: String?) {
        if (selectedItem != null) {
            availableColumns.add(selectedItem)
            includedColumns.remove(selectedItem)
        }
    }

    private fun parse(structure: FileStructure) {
        fieldList = structure.readStructure()
        includedColumns.clear()
        availableColumns.clear()
        availableColumns.addAll(fieldList.map { it.name })
    }
}