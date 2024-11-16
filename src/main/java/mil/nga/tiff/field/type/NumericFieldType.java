package mil.nga.tiff.field.type;

import mil.nga.tiff.io.ByteReader;

import java.nio.ByteBuffer;

abstract public sealed class NumericFieldType<T extends Number> extends SingleValueFieldType<T> permits ByteField, ShortField, LongField, FloatField, DoubleField {

    /**
     * Reads sample from given buffer
     *
     * @param buffer A buffer to read from. @note Make sure position is set.
     * @return Sample from buffer
     */
    abstract protected T readSample(ByteBuffer buffer);

    /**
     * Writes sample into given buffer.
     *
     * @param buffer A buffer to write to. @note Make sure buffer position is set.
     * @param value  Actual value to write.
     */
    abstract protected void writeSample(ByteBuffer buffer, T value);


    /**
     * Updates sample to given value in buffer.
     *
     * @param buffer      A buffer to be updated.
     * @param bufferIndex Position in buffer where to update.
     * @param sampleIndex Sample index in sampleFieldTypes. Needed for determining sample size.
     * @param value       A Number value to be put in buffer. Has to be same size as sampleFieldTypes[sampleIndex].
     */
    public void updateSampleInByteBuffer(ByteBuffer buffer, int bufferIndex, int sampleIndex, T value) {
        if (bufferIndex < 0 || bufferIndex >= buffer.capacity()) {
            throw new IndexOutOfBoundsException("index: " + bufferIndex + ". Buffer capacity: " + buffer.capacity());
        }

        buffer.position(bufferIndex);
        writeSample(buffer, value);
    }

    /**
     * Reads sample from given buffer.
     *
     * @param buffer      A buffer to read from
     * @param index       Position in buffer where to read from
     * @param sampleIndex Index of sample type to read
     * @return Number read from buffer
     */
    public T getSampleFromByteBuffer(ByteBuffer buffer, int index, int sampleIndex) {
        if (index < 0 || index >= buffer.capacity()) {
            throw new IndexOutOfBoundsException("Requested index: " + index + ", but size of buffer is: " + buffer.capacity());
        }

        buffer.position(index);
        return readSample(buffer);
    }

    /**
     * Transfers sample from input buffer to given output buffer.
     *
     * @param outBuffer A buffer to write to. @note Make sure buffer position is set.
     * @param inBuffer  A buffer to read from. @note Make sure buffer position is set.
     */
    abstract public void transferSample(ByteBuffer outBuffer, ByteBuffer inBuffer);

}
