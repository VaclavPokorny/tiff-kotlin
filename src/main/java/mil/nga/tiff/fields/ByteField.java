package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffConstants;

/**
 * 8-bit unsigned integer
 */
public final class ByteField extends AbstractFieldType {
    public ByteField() {
        super(1, TiffConstants.SAMPLE_FORMAT_UNSIGNED_INT);
    }

    @Override
    public Number readRasterValueFromReader(ByteReader reader) {
        return reader.readUnsignedByte();
    }
}
