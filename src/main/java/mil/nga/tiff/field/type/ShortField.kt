package mil.nga.tiff.field.type

import java.nio.ByteBuffer

abstract class ShortField<T : Number> : NumericFieldType<T>() {
    override fun writeSample(buffer: ByteBuffer, value: T) {
        buffer.putShort(value.toShort())
    }

    override fun transferSample(outBuffer: ByteBuffer, inBuffer: ByteBuffer) {
        outBuffer.putShort(inBuffer.getShort())
    }
}
