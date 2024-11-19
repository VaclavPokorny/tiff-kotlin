package mil.nga.tiff.field.type

import mil.nga.tiff.field.FieldType
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter
import java.io.IOException

/**
 * An 8-bit byte that may contain anything, depending on the definition of
 * the field
 */
@FieldType(id = 7, bytesPerSample = 1)
class UndefinedField : SingleValueFieldType<Short>() {
    override fun readValue(reader: ByteReader): Short {
        return reader.readUnsignedByte()
    }

    @Throws(IOException::class)
    override fun writeValue(writer: ByteWriter, value: Short): Int {
        writer.writeUnsignedByte(value)
        return 1
    }
}
