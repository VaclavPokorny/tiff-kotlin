package mil.nga.tiff.field.type

import mil.nga.tiff.field.FieldType
import mil.nga.tiff.field.type.enumeration.SampleFormat
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter
import java.io.IOException
import java.nio.ByteBuffer

/**
 * A 16-bit (2-byte) signed (twos-complement) integer
 */
@FieldType(id = 8, bytesPerSample = 2, sampleFormat = SampleFormat.SIGNED_INT)
class SignedShortField : ShortField<Short>() {
    override fun readValue(reader: ByteReader): Short {
        return reader.readShort()
    }

    override fun readSample(buffer: ByteBuffer): Short {
        return buffer.getShort()
    }

    @Throws(IOException::class)
    override fun writeValue(writer: ByteWriter, value: Short): Int {
        writer.writeShort(value)
        return metadata().bytesPerSample
    }
}
