package mil.nga.tiff.field.type

import mil.nga.tiff.field.FieldType
import mil.nga.tiff.field.type.enumeration.SampleFormat
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter
import java.io.IOException
import java.nio.ByteBuffer

/**
 * A 32-bit (4-byte) signed (twos-complement) integer
 */
@FieldType(id = 9, bytesPerSample = 4, sampleFormat = SampleFormat.SIGNED_INT)
class SignedLongField : LongField<Int>() {
    override fun readValue(reader: ByteReader): Int {
        return reader.readInt()
    }

    override fun readSample(buffer: ByteBuffer): Int {
        return buffer.getInt()
    }

    @Throws(IOException::class)
    override fun writeValue(writer: ByteWriter, value: Int): Int {
        writer.writeInt(value)
        return metadata().bytesPerSample
    }
}
