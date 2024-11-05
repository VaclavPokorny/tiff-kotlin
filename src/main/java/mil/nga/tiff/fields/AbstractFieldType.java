package mil.nga.tiff.fields;

import mil.nga.tiff.internal.FileDirectoryEntry;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.util.TiffConstants;
import mil.nga.tiff.util.TiffException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

abstract public sealed class AbstractFieldType permits AbstractRasterFieldType, ASCIIField, UndefinedField, AbstractRationalField {

    /**
     * Number of bytes per field value
     */
    private final int bytes;
    private final int sampleFormat;

    public AbstractFieldType(int bytes) {
        this.bytes = bytes;
        this.sampleFormat = TiffConstants.SampleFormat.UNDEFINED;
    }

    public AbstractFieldType(int bytes, int sampleFormat) {
        this.bytes = bytes;
        this.sampleFormat = sampleFormat;
    }

    public boolean hasSampleFormat(int sampleFormat) {
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
    public int getSampleFormat() {
        if (sampleFormat == TiffConstants.SampleFormat.UNDEFINED) {
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

    public Number readSample(ByteBuffer buffer) {
        throw new TiffException("Unsupported raster field type.");
    }

    public void writeSample(ByteBuffer buffer, Number value) {
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
    abstract public int writeValues(ByteWriter writer, FileDirectoryEntry entry) throws IOException;

}
