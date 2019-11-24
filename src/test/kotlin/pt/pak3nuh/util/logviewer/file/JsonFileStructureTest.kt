package pt.pak3nuh.util.logviewer.file

import assertk.all
import assertk.assertThat
import assertk.assertions.containsAll
import assertk.assertions.containsExactly
import assertk.assertions.extracting
import org.junit.jupiter.api.Test

internal class JsonFileStructureTest {

    @Test
    fun readStructure() {
        val tempFile = writeTempFile("""{ "key1":"val1", "key2":0 }""")
        val structure = JsonFileStructure(tempFile.toFile())
        val list = structure.readStructure()
        assertThat(list).all {
            extracting { it.name }.containsAll("key1", "key2")
            extracting { it.position }.containsAll(0, 1)
        }
    }


    @Test
    fun `multiple line json`() {
        val json = """
            { "key1":"val1" }
            { "key2":"val2" }
        """.trimIndent()
        val tempFile = writeTempFile(json)
        val structure = JsonFileStructure(tempFile.toFile())
        val list = structure.readStructure()
        assertThat(list).containsExactly(FileField(0, "key1"))
    }
}