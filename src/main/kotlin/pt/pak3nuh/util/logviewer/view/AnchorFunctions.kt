package pt.pak3nuh.util.logviewer.view

import javafx.scene.Node
import javafx.scene.layout.AnchorPane

fun <T : Node> T.anchor(top: Double? = null, bottom: Double? = null, left: Double? = null, right: Double? = null): T {
    return apply {
        if (top != null) AnchorPane.setTopAnchor(this, top)
        if (bottom != null) AnchorPane.setBottomAnchor(this, bottom)
        if (left != null) AnchorPane.setLeftAnchor(this, left)
        if (right != null) AnchorPane.setRightAnchor(this, right)
    }
}

fun <T : Node> T.anchorAll(value: Double = 0.0): T = anchor(value, value, value, value)