package mil.nga.tiff.field.type;

import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.internal.FileDirectoryEntry;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

abstract public sealed class AbstractRasterFieldType extends AbstractFieldType permits AbstractByteField, AbstractShortField, AbstractLongField, FloatField, DoubleField {

    public AbstractRasterFieldType(int bytes) {
        super(bytes);
    }

    public AbstractRasterFieldType(int bytes, SampleFormat sampleFormat) {
        super(bytes, sampleFormat);
    }

    /**
     * Read the value from the reader according to the field type
     *
     * @param reader byte reader
     * @return value
     */
    @Override
    abstract public Number readValue(ByteReader reader);

    /**
     * Reads sample from given buffer
     *
     * @param buffer A buffer to read from. @note Make sure position is set.
     * @return Sample from buffer
     */
    abstract protected Number readSample(ByteBuffer buffer);

    /**
     * Writes sample into given buffer.
     *
     * @param buffer A buffer to write to. @note Make sure buffer position is set.
     * @param value  Actual value to write.
     */
    abstract protected void writeSample(ByteBuffer buffer, Number value);



    /**
     * Updates sample to given value in buffer.
     *
     * @param buffer      A buffer to be updated.
     * @param bufferIndex Position in buffer where to update.
     * @param sampleIndex Sample index in sampleFieldTypes. Needed for determining
     *                    sample size.
     * @param value       A Number value to be put in buffer. Has to be same size as
     *                    sampleFieldTypes[sampleIndex].
     */
    public void updateSampleInByteBuffer(ByteBuffer buffer, int bufferIndex, int sampleIndex, Number value) {
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
    public Number getSampleFromByteBuffer(ByteBuffer buffer, int index, int sampleIndex) {
        if (index < 0 || index >= buffer.capacity()) {
            throw new IndexOutOfBoundsException("Requested index: " + index + ", but size of buffer is: " + buffer.capacity());
        }

        buffer.position(index);
        return readSample(buffer);
    }




    /**
     * Writes sample from input buffer to given output buffer.
     *
     * @param outBuffer A buffer to write to. @note Make sure buffer position is set.
     * @param inBuffer  A buffer to read from. @note Make sure buffer position is set.
     */
    @Override
    abstract public void writeSample(ByteBuffer outBuffer, ByteBuffer inBuffer);

    @Override
    public List<Object> getDirectoryEntryValues(ByteReader reader, long typeCount) {

        List<Object> values = new ArrayList<>();

        for (int i = 0; i < typeCount; i++) {
            values.add(readValue(reader));
        }

        return values;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int writeDirectoryEntryValues(ByteWriter writer, FileDirectoryEntry entry) throws IOException {
        List<Object> valuesList;
        if (entry.typeCount() == 1 && !entry.fieldTag().isArray()) {
            valuesList = new ArrayList<>();
            valuesList.add(entry.values());
        } else {
            valuesList = (List<Object>) entry.values();
        }

        int bytesWritten = 0;

        for (Object value : valuesList) {
            writeValue(writer, value);
            bytesWritten += getBytes();
        }

        return bytesWritten;
    }

    /**
     * Write value
     *
     * @param writer byte writer
     * @param value  value
     */
    abstract protected void writeValue(ByteWriter writer, Object value) throws IOException;

}
