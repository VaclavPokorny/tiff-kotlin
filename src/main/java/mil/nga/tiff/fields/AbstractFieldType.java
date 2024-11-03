package mil.nga.tiff.fields;

import mil.nga.tiff.FieldType;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffConstants;
import mil.nga.tiff.util.TiffException;

abstract public sealed class AbstractFieldType permits ByteField, ASCIIField, ShortField, LongField, RationalField, SignedByteField, UndefinedField, SignedShortField, SignedLongField, SignedRationalField, FloatField, DoubleField {

    /**
     * Number of bytes per field value
     */
    private final int bytes;
    private final int sampleFormat;

    public AbstractFieldType(int bytes) {
        this.bytes = bytes;
        this.sampleFormat = TiffConstants.SAMPLE_FORMAT_UNDEFINED;
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

    public int getSampleFormat() {
        if (sampleFormat == TiffConstants.SAMPLE_FORMAT_UNDEFINED) {
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

    abstract public Number readRasterValueFromReader(ByteReader reader);

}
