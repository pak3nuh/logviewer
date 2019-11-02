package pt.pak3nuh.util.logviewer.view

import javafx.application.Platform.runLater
import javafx.collections.transformation.FilteredList
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.SplitPane
import javafx.stage.FileChooser
import pt.pak3nuh.util.logviewer.file.FileChangeNotifier
import pt.pak3nuh.util.logviewer.util.Logger
import tornadofx.*
import java.io.File

private val logger = Logger.createLogger<MainView>()

class MainView : View("Logviewer") {

    private val lineList = observableListOf<String>()
    private var notifier: FileChangeNotifier? = null
    private lateinit var filterPane: SplitPane

    override val root: Parent = borderpane {

        // Open, Clear, Settings
        top = hbox {
            button("Open File").action(::selectFile)
            button("Clear").action(lineList::clear)
            button("Settings")
        }

        // lines
        center = anchorpane {
            splitpane(Orientation.VERTICAL) {
                anchorpane {
                    listview(lineList).anchorAll()
                }

                vbox {
                    // filter application field
                    hbox {
                        val filter = textfield {
                            prefWidth = 200.0
                            promptText = "Filter text"
                        }
                        button {
                            text = "Contains Filter"
                            action {
                                addFilterView(filter.text, false)
                            }
                        }
                        button {
                            text = "Regex Filter"
                            action {
                                addFilterView(filter.text, true)
                            }
                        }
                    }

                    // multiple list view
                    anchorpane {
                        filterPane = splitpane(Orientation.VERTICAL).anchorAll()
                    }
                }
                anchorAll()
            }
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
                val predicate: (CharSequence) -> Boolean =
                        if (regex) { it -> Regex(filter).matches(it) } else { it -> filter in it }
                center = listview(FilteredList(lineList, predicate))
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