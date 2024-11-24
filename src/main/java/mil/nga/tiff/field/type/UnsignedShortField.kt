package mil.nga.tiff.field.type

import mil.nga.tiff.field.FieldType
import mil.nga.tiff.field.type.enumeration.SampleFormat
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter
import java.io.IOException
import java.nio.ByteBuffer

/**
 * 16-bit (2-byte) unsigned integer
 */
@FieldType(id = 3, bytesPerSample = 2, sampleFormat = SampleFormat.UNSIGNED_INT)
object UnsignedShortField : ShortField<Int>() {
    override fun readValue(reader: ByteReader): Int {
        return reader.readUnsignedShort()
    }

    override fun readSample(buffer: ByteBuffer): Int {
        return buffer.getShort().toInt() and 0xffff
    }

    @Throws(IOException::class)
    override fun writeValue(writer: ByteWriter, value: Int): Int {
        writer.writeUnsignedShort(value)
        return metadata().bytesPerSample
    }
}
