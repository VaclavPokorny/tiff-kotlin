package mil.nga.tiff.field.type

import mil.nga.tiff.field.FieldType
import mil.nga.tiff.field.type.enumeration.SampleFormat
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter
import java.io.IOException
import java.nio.ByteBuffer

/**
 * Single precision (4-byte) IEEE format
 */
@FieldType(name = "FLOAT", id = 11, bytesPerSample = 4, sampleFormat = SampleFormat.FLOAT)
object FloatField : NumericFieldType<Float>() {
    override fun readValue(reader: ByteReader): Float {
        return reader.readFloat()
    }

    override fun readSample(buffer: ByteBuffer): Float {
        return buffer.getFloat()
    }

    override fun writeSample(buffer: ByteBuffer, value: Float) {
        buffer.putFloat(value)
    }

    override fun transferSample(outBuffer: ByteBuffer, inBuffer: ByteBuffer) {
        outBuffer.putFloat(inBuffer.getFloat())
    }

    @Throws(IOException::class)
    override fun writeValue(writer: ByteWriter, value: Float): Int {
        writer.writeFloat(value)
        return metadata().bytesPerSample
    }
}
