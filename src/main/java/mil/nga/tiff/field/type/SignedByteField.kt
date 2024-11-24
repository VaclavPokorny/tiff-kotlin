package mil.nga.tiff.field.type

import mil.nga.tiff.field.FieldType
import mil.nga.tiff.field.type.enumeration.SampleFormat
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter
import java.io.IOException
import java.nio.ByteBuffer

/**
 * An 8-bit signed (twos-complement) integer
 */
@FieldType(name = "SBYTE", id = 6, bytesPerSample = 1, sampleFormat = SampleFormat.SIGNED_INT)
object SignedByteField : ByteField<Byte>() {
    override fun readValue(reader: ByteReader): Byte {
        return reader.readByte()
    }

    override fun readSample(buffer: ByteBuffer): Byte {
        return buffer.get()
    }

    @Throws(IOException::class)
    override fun writeValue(writer: ByteWriter, value: Byte): Int {
        writer.writeByte(value)
        return metadata().bytesPerSample
    }
}
