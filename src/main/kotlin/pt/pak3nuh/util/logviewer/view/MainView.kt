package pt.pak3nuh.util.logviewer.view

import javafx.scene.Parent
import javafx.scene.control.TabPane
import javafx.stage.FileChooser
import pt.pak3nuh.util.logviewer.util.Logger
import pt.pak3nuh.util.logviewer.view.control.LogFileTab
import tornadofx.*
import java.io.File
import java.nio.file.Paths

private val logger = Logger.createLogger<MainView>()

class MainView : View("Logviewer") {

    private lateinit var tabPane: TabPane

    override val root: Parent = borderpane {

        // Open, Clear, Settings
        top = hbox {
            button("Open File").action(::selectFile)
            button("Clear").action {
                (tabPane.selectionModel.selectedItem as? LogFileTab)?.clearLines()
            }
            button("Settings")
        }

        // tabs
        center = anchorpane {
            tabPane = tabpane().anchorAll()
        }
    }

    init {
        this.currentWindow?.apply {
            width = 500.0
            height = 500.0
        }
    }

    private fun addFilterView(filter: String, regex: Boolean) {
        logger.debug("Applying filter %s regex %b", filter, regex)
        filterPane.apply {
            val container = borderpane()
            container.apply {
                top = hbox {
                    val filterLabel = if (regex) "Regex" else "Contains"
                    label("""$filterLabel filtering by "$filter"""")
                }
                right = button("X") {
                    action { filterPane.items.remove(container) }
                }
                val predicate: (LogItem) -> Boolean =
                        if (regex) { it -> Regex(filter).matches(it.message) } else { it -> filter in it.message }
                center = listview(FilteredList(lineList, predicate)) {
                    selectionModel.selectionMode = SelectionMode.SINGLE
                    onMouseClicked = EventHandler {
                        val selectedItem = this.selectionModel.selectedItem
                        mainView.selectionModel.select(selectedItem)
                        mainView.scrollTo(selectedItem)
                    }
                }
            }
        }
    }

    private fun selectFile() {
        val chooser = FileChooser()
        val file: File? = chooser.showOpenDialog(this.currentWindow)
        if (file != null) {
            addFileTab(file)
        }
    }

    private fun addFileTab(file: File) {
        logger.info("Opening file %s", file)
        tabPane.tabs.add(LogFileTab(file))
    }
}