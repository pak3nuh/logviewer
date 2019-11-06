package pt.pak3nuh.util.logviewer.view

import javafx.scene.Parent
import javafx.scene.control.TabPane
import javafx.stage.FileChooser
import pt.pak3nuh.util.logviewer.util.Logger
import pt.pak3nuh.util.logviewer.view.control.LogFileTab
import tornadofx.*
import java.io.File

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