package mil.nga.tiff.field.type

import mil.nga.tiff.field.FieldType
import mil.nga.tiff.field.type.enumeration.SampleFormat
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter
import java.io.IOException
import java.nio.ByteBuffer

/**
 * Double precision (8-byte) IEEE format
 */
@FieldType(id = 12, bytesPerSample = 8, sampleFormat = SampleFormat.FLOAT)
class DoubleField : NumericFieldType<Double>() {
    override fun readValue(reader: ByteReader): Double {
        return reader.readDouble()
    }

    override fun readSample(buffer: ByteBuffer): Double {
        return buffer.getDouble()
    }

    override fun writeSample(buffer: ByteBuffer, value: Double) {
        buffer.putDouble(value)
    }

    override fun transferSample(outBuffer: ByteBuffer, inBuffer: ByteBuffer) {
        outBuffer.putDouble(inBuffer.getDouble())
    }

    @Throws(IOException::class)
    override fun writeValue(writer: ByteWriter, value: Double): Int {
        writer.writeDouble(value)
        return metadata().bytesPerSample
    }
}
