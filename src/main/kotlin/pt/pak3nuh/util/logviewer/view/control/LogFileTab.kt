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

class LogFileTab(val file: File, settings: Settings = Settings.createDefault()) : Tab(file.name) {

    private val logger = Logger.createLogger<LogFileTab>(file.name)

    private val lineList = observableListOf<LogItem>()
    private var notifier: FileChangeNotifier? = null
    // todo this is ugly, already in settings
    private var itemFactory: ItemFactory = settings.itemFactory
    private lateinit var filterPane: SplitPane
    private lateinit var mainView: TableView<LogItem>

    var settings: Settings = settings
        set(value) {
            onNewSettings(value)
        }

    init {
        openFile(file)
        setOnClosed { notifier?.close() }
        content = borderpane {

            // lines
            center = anchorpane {
                splitpane(Orientation.VERTICAL) {
                    anchorpane {
                        mainView = tableview(lineList)
                        mainView.apply {
                            isEditable = false
                            configureColumns(settings.columns)
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

    private fun onNewSettings(settings: Settings) {
        logger.debug("Applying new settings")
        itemFactory = settings.itemFactory
        val newList = lineList.map {
            itemFactory(it.asString)
        }
        lineList.clear()
        lineList.addAll(newList)
        configureColumns(settings.columns)
    }

    private fun configureColumns(columns: List<ColumnDefinition>) {
        logger.trace("Configuring columns")
        val list = columns + ColumnDefinition("Full message") { logItem -> logItem.asString }
        val elements = list.map { colDef ->
            val column = TableColumn<LogItem, String>(colDef.name)
            column.cellValueFactory = Callback {
                // todo called multiple times for the same field??
                stringProperty(colDef.getter(it.value))
            }
            column
        }
        mainView.columns.clear()
        mainView.columns.addAll(elements)
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
                        if (regex) { it -> Regex(filter).matches(it.asString) } else { it -> filter in it.asString }
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
                lineList.addAll(it.map(itemFactory))
            }
        }
        notifier.start()
        this.notifier = notifier
    }

    fun clearLines() {
        lineList.clear()
    }
}
