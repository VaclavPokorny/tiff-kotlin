package mil.nga.tiff.field.type

import mil.nga.tiff.field.FieldType
import mil.nga.tiff.field.type.enumeration.SampleFormat
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter
import java.nio.ByteBuffer

/**
 * 8-bit unsigned integer
 */
@FieldType(id = 1, bytesPerSample = 1, sampleFormat = SampleFormat.UNSIGNED_INT)
object UnsignedByteField : ByteField<Short>() {
    override fun readValue(reader: ByteReader): Short {
        return reader.readUnsignedByte()
    }

    override fun readSample(buffer: ByteBuffer): Short {
        return (buffer.get().toInt() and 0xff).toShort()
    }

    override fun writeValue(writer: ByteWriter, value: Short): Int {
        writer.writeUnsignedByte(value)
        return metadata().bytesPerSample
    }
}
