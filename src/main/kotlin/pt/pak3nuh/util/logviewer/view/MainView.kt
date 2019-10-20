package pt.pak3nuh.util.logviewer.view

import javafx.scene.Parent
import javafx.stage.FileChooser
import pt.pak3nuh.util.logviewer.file.FileChangeNotifier
import pt.pak3nuh.util.logviewer.util.Logger
import tornadofx.*
import java.io.File

private val logger = Logger.createLogger<MainView>()

class MainView : View("Logviewer") {

    private val lineList = observableListOf<String>()
    private var notifier: FileChangeNotifier? = null

    override val root: Parent = borderpane {
        // Open, Clear, Settings
        top = hbox {
            button("Open") { action(::selectFile) }
            button("Clear") { lineList.clear() }
            button("Settings")
        }

        // lines
        center = pane {
            listview(lineList)
        }

        // filters
        bottom = borderpane {
            // filter box
            top = hbox { }

            // multiple list view
            center = pane { }
        }
    }

    private fun selectFile() {
        val chooser = FileChooser()
        val file: File? = chooser.showOpenDialog(this.currentWindow)
        if (file != null) {
            logger.info("Opening file %s", file)
            openFile(file)
        }
    }

    private fun openFile(file: File) {
        require(file.exists()) { "File must exist" }
        this.notifier?.close()
        lineList.clear()
        val notifier = FileChangeNotifier(file.toPath())
        notifier.onNewLines { lineList.addAll(it) }
        notifier.start()
        this.notifier = notifier
    }
}