package pt.pak3nuh.util.logviewer.view.control

import javafx.application.Platform
import javafx.collections.transformation.FilteredList
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.control.*
import javafx.util.Callback
import pt.pak3nuh.util.logviewer.data.LogItem
import pt.pak3nuh.util.logviewer.file.FileChangeNotifier
import pt.pak3nuh.util.logviewer.util.Logger
import pt.pak3nuh.util.logviewer.view.anchorAll
import tornadofx.*
import java.io.File

class LogFileTab(file: File) : Tab(file.name) {

    private val logger = Logger.createLogger<LogFileTab>(file.name)

    private val lineList = observableListOf<LogItem>()
    private var notifier: FileChangeNotifier? = null
    private val settings = Settings()
    private lateinit var filterPane: SplitPane
    private lateinit var mainView: TableView<LogItem>

    init {
        openFile(file)
        setOnClosed { notifier?.close() }
        content = borderpane {

            // lines
            center = anchorpane {
                splitpane(Orientation.VERTICAL) {
                    anchorpane {
                        mainView = tableview(lineList) {
                            isEditable = false
                            configureColumns(this)
                        }.anchorAll()
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
    }

    private fun configureColumns(tableView: TableView<LogItem>) {
        val columns = tableView.columns
        columns.clear()
        columns.addAll(settings.columns.map { colDef ->
            val column = TableColumn<LogItem, String>(colDef.name)
            column.cellValueFactory = Callback {
                stringProperty(colDef.getter(it.value))
            }
            column
        })
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

    private fun openFile(file: File) {
        require(file.exists()) { "File must exist" }
        this.notifier?.close()
        lineList.clear()
        val notifier = FileChangeNotifier(file.toPath())
        notifier.onNewLines {
            Platform.runLater {
                lineList.addAll(it.map(::LogItem))
            }
        }
        notifier.start()
        this.notifier = notifier
    }

    fun clearLines() {
        lineList.clear()
    }
}
