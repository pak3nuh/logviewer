package pt.pak3nuh.util.logviewer.view.control

import javafx.collections.ObservableList
import javafx.scene.control.ListView

class FilteredListView<T>(items: ObservableList<T>, val filter: (T) -> Boolean) : ListView<T>(items) {

}