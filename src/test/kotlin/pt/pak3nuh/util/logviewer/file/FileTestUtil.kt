package pt.pak3nuh.util.logviewer.file

import java.nio.file.Files
import java.nio.file.Path

fun writeTempFile(fileContent: String): Path {
    val tempFile = Files.createTempFile("myfile", "")
    Files.writeString(tempFile, fileContent)
    return tempFile
}