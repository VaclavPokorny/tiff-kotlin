package mil.nga.tiff.field.type

import mil.nga.tiff.field.FieldType
import mil.nga.tiff.field.type.enumeration.SampleFormat
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter
import java.io.IOException
import java.nio.ByteBuffer

/**
 * 32-bit (4-byte) unsigned integer
 */
@FieldType(id = 4, bytesPerSample = 4, sampleFormat = SampleFormat.UNSIGNED_INT)
class UnsignedLongField : LongField<Long>() {
    override fun readValue(reader: ByteReader): Long {
        return reader.readUnsignedInt()
    }

    override fun readSample(buffer: ByteBuffer): Long {
        return buffer.getInt().toLong() and 0xffffffffL
    }

    @Throws(IOException::class)
    override fun writeValue(writer: ByteWriter, value: Long): Int {
        writer.writeUnsignedInt(value)
        return metadata().bytesPerSample
    }
}
