package mil.nga.tiff.field.type

import mil.nga.tiff.field.tag.FieldTagType
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter
import java.io.IOException

abstract class SingleValueFieldType<T> : GenericFieldType<T> {
    override fun readDirectoryEntryValues(reader: ByteReader, typeCount: Long): List<T> {
        val values: MutableList<T> = ArrayList()

        for (i in 0..<typeCount) {
            values.add(readValue(reader))
        }

        return values
    }

    /**
     * Read the value from the reader according to the field type
     *
     * @param reader byte reader
     * @return value
     */
    abstract fun readValue(reader: ByteReader): T

    @Throws(IOException::class)
    override fun writeDirectoryEntryValues(
        writer: ByteWriter,
        fieldTag: FieldTagType?,
        typeCount: Long,
        values: List<T>
    ): Int {
        var bytesWritten = 0

        for (value in values) {
            bytesWritten += writeValue(writer, value)
        }

        return bytesWritten
    }

    /**
     * Write value
     *
     * @param writer byte writer
     * @param value  value
     */
    @Throws(IOException::class)
    protected abstract fun writeValue(writer: ByteWriter, value: T): Int
}
