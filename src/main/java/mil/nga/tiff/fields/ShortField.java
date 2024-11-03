package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffConstants;

/**
 * 16-bit (2-byte) unsigned integer
 */
public final class ShortField extends AbstractFieldType {
    public ShortField() {
        super(2, TiffConstants.SAMPLE_FORMAT_UNSIGNED_INT);
    }

    @Override
    public Number readRasterValueFromReader(ByteReader reader) {
        return reader.readUnsignedShort();
    }
}
