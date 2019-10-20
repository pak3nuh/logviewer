package pt.pak3nuh.util.logviewer.view

import javafx.scene.Parent
import tornadofx.*

class MainView : View("Logviewer") {

    private val lineList = observableListOf<String>()

    override val root: Parent = borderpane {
        // Open, Clear, Settings
        top = hbox {
            button("Open") { action(::openFile) }
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

    private fun openFile() {
        TODO("not implemented")
    }
}