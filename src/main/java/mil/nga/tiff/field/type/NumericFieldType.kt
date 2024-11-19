package mil.nga.tiff.field.type

import java.nio.ByteBuffer

abstract class NumericFieldType<T : Number?> : SingleValueFieldType<T>() {
    /**
     * Reads sample from given buffer
     *
     * @param buffer A buffer to read from. @note Make sure position is set.
     * @return Sample from buffer
     */
    protected abstract fun readSample(buffer: ByteBuffer): T

    /**
     * Writes sample into given buffer.
     *
     * @param buffer A buffer to write to. @note Make sure buffer position is set.
     * @param value  Actual value to write.
     */
    protected abstract fun writeSample(buffer: ByteBuffer, value: T)


    /**
     * Updates sample to given value in buffer.
     *
     * @param buffer      A buffer to be updated.
     * @param bufferIndex Position in buffer where to update.
     * @param sampleIndex Sample index in sampleFieldTypes. Needed for determining sample size.
     * @param value       A Number value to be put in buffer. Has to be same size as sampleFieldTypes[sampleIndex].
     */
    @Suppress("UNCHECKED_CAST")
    fun updateSampleInByteBuffer(buffer: ByteBuffer, bufferIndex: Int, sampleIndex: Int, value: Number) {
        if (bufferIndex < 0 || bufferIndex >= buffer.capacity()) {
            throw IndexOutOfBoundsException("index: " + bufferIndex + ". Buffer capacity: " + buffer.capacity())
        }

        buffer.position(bufferIndex)
        writeSample(buffer, value as T)
    }

    /**
     * Reads sample from given buffer.
     *
     * @param buffer      A buffer to read from
     * @param index       Position in buffer where to read from
     * @param sampleIndex Index of sample type to read
     * @return Number read from buffer
     */
    fun getSampleFromByteBuffer(buffer: ByteBuffer, index: Int, sampleIndex: Int): T {
        if (index < 0 || index >= buffer.capacity()) {
            throw IndexOutOfBoundsException("Requested index: " + index + ", but size of buffer is: " + buffer.capacity())
        }

        buffer.position(index)
        return readSample(buffer)
    }

    /**
     * Transfers sample from input buffer to given output buffer.
     *
     * @param outBuffer A buffer to write to. @note Make sure buffer position is set.
     * @param inBuffer  A buffer to read from. @note Make sure buffer position is set.
     */
    abstract fun transferSample(outBuffer: ByteBuffer, inBuffer: ByteBuffer)
}
