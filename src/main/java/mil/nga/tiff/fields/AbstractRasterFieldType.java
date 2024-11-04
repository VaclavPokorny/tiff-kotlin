package mil.nga.tiff.fields;

import mil.nga.tiff.FieldType;
import mil.nga.tiff.FileDirectoryEntry;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

abstract public sealed class AbstractRasterFieldType extends AbstractFieldType permits ByteField, ShortField, LongField, SignedByteField, SignedShortField, SignedLongField, FloatField, DoubleField {

    public AbstractRasterFieldType(int bytes) {
        super(bytes);
    }

    public AbstractRasterFieldType(int bytes, int sampleFormat) {
        super(bytes, sampleFormat);
    }

    /**
     * Read the value from the reader according to the field type
     *
     * @param reader    byte reader
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
    @Override
    abstract public Number readSample(ByteBuffer buffer);

    /**
     * Writes sample into given buffer.
     *
     * @param buffer A buffer to write to. @note Make sure buffer position is set.
     * @param value  Actual value to write.
     */
    @Override
    abstract public void writeSample(ByteBuffer buffer, Number value);

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
    public int writeValues(ByteWriter writer, FileDirectoryEntry entry) throws IOException {
        List<Object> valuesList;
        if (entry.getTypeCount() == 1 && !entry.getFieldTag().isArray()) {
            valuesList = new ArrayList<>();
            valuesList.add(entry.getValues());
        } else {
            valuesList = (List<Object>) entry.getValues();
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
     * @param writer
     *            byte writer
     * @param value
     *            value
     */
    abstract protected void writeValue(ByteWriter writer, Object value) throws IOException;

}
