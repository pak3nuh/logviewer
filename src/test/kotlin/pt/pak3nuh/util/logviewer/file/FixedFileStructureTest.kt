package pt.pak3nuh.util.logviewer.file

import assertk.assertThat
import assertk.assertions.containsExactly
import org.junit.jupiter.api.Test

internal class FixedFileStructureTest {

    @Test
    internal fun `read file structure with a separator char`() {
        val tempFile = writeTempFile("k1|k2|k3")
        val structure = FixedFileStructure(tempFile.toFile(), "|")
        val list = structure.readStructure()
        assertThat(list).containsExactly(
                FileField(0, "Field0"),
                FileField(1, "Field1"),
                FileField(2, "Field2")
        )
    }
}