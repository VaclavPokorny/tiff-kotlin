package mil.nga.tiff.field.type

import java.nio.ByteBuffer

abstract class LongField<T : Number> : NumericFieldType<T>() {
    override fun writeSample(buffer: ByteBuffer, value: T) {
        buffer.putInt(value.toInt())
    }

    override fun transferSample(outBuffer: ByteBuffer, inBuffer: ByteBuffer) {
        outBuffer.putInt(inBuffer.getInt())
    }
}
