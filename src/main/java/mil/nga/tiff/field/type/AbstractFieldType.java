package mil.nga.tiff.field.type;

import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.internal.FileDirectoryEntry;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.util.TiffException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

abstract public sealed class AbstractFieldType permits AbstractRasterFieldType, ASCIIField, UndefinedField, AbstractRationalField {

    /**
     * Number of bytes per field value
     */
    private final int bytes;
    private final SampleFormat sampleFormat;

    public AbstractFieldType(int bytes) {
        this.bytes = bytes;
        this.sampleFormat = SampleFormat.UNDEFINED;
    }

    public AbstractFieldType(int bytes, SampleFormat sampleFormat) {
        this.bytes = bytes;
        this.sampleFormat = sampleFormat;
    }

    public boolean hasSampleFormat(SampleFormat sampleFormat) {
        return this.sampleFormat == sampleFormat;
    }

    public boolean hasBitsPerSample(int bitsPerSample) {
        return getBits() == bitsPerSample;
    }

    /**
     * Get the sample format of the field type
     *
     * @return sample format
     * @since 2.0.0
     */
    public SampleFormat getSampleFormat() {
        if (sampleFormat == SampleFormat.UNDEFINED) {
            throw new TiffException("Unsupported sample format");
        }
        return sampleFormat;
    }

    public int getBytes() {
        return bytes;
    }

    public int getBits() {
        return bytes * 8;
    }

    public Number readValue(ByteReader reader) {
        throw new TiffException("Unsupported raster field type.");
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
        throw new TiffException("Unsupported raster field type.");
    }

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
        throw new TiffException("Unsupported raster field type.");
    }

    public void writeSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        throw new TiffException("Unsupported raster field type.");
    }

    /**
     * Get the internal entry values
     *
     * @param reader    byte reader
     * @param typeCount type count
     * @return values
     */
    abstract public List<Object> getDirectoryEntryValues(ByteReader reader, long typeCount);

    /**
     * Write file internal entry values
     *
     * @param writer byte writer
     * @param entry  file internal entry
     * @return bytes written
     * @throws IOException IO exception
     */
    abstract public int writeDirectoryEntryValues(ByteWriter writer, FileDirectoryEntry entry) throws IOException;

}
