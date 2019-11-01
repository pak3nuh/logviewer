package pt.pak3nuh.util.logviewer.view

import javafx.application.Platform.runLater
import javafx.collections.transformation.FilteredList
import javafx.scene.Parent
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import pt.pak3nuh.util.logviewer.file.FileChangeNotifier
import pt.pak3nuh.util.logviewer.util.Logger
import tornadofx.*
import java.io.File

private val logger = Logger.createLogger<MainView>()

class MainView : View("Logviewer") {

    private val lineList = observableListOf<String>()
    private var notifier: FileChangeNotifier? = null
    private lateinit var filterBox: VBox

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
            top = hbox {
                val filter = textfield {
                    promptText = "Filter text"
                }
                label("Regex")
                val regex = checkbox()
                button {
                    text = "Apply"
                    action {
                        addFilterPane(filter.text, regex.isSelected)
                    }
                }

            }

            // multiple list view
            center = pane {
                filterBox = vbox()
            }
        }
    }

    private fun addFilterPane(filter: String, regex: Boolean) {
        logger.debug("Applying filter %s regex %b", filter, regex)
        filterBox.apply {
            val box = vbox()
            box.apply {
                hbox {
                    label("Filtering by $filter")
                    button("X") {
                        action { filterBox.children.remove(box) }
                    }
                }
                val predicate: (CharSequence) -> Boolean =
                        if (regex) { it -> Regex(filter).matches(it) } else { it -> filter in it }
                add(listview(FilteredList(lineList, predicate)))
            }
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
        notifier.onNewLines {
            runLater {
                lineList.addAll(it)
            }
        }
        notifier.start()
        this.notifier = notifier
    }
}