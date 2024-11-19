package mil.nga.tiff.field.type

import java.nio.ByteBuffer

abstract class ByteField<T : Number> : NumericFieldType<T>() {
    override fun writeSample(buffer: ByteBuffer, value: T) {
        buffer.put(value.toByte())
    }

    override fun transferSample(outBuffer: ByteBuffer, inBuffer: ByteBuffer) {
        outBuffer.put(inBuffer.get())
    }
}
