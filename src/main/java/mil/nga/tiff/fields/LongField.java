package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffConstants;

/**
 * 32-bit (4-byte) unsigned integer
 */
public final class LongField extends AbstractFieldType {
    public LongField() {
        super(4, TiffConstants.SAMPLE_FORMAT_UNSIGNED_INT);
    }

    @Override
    public Number readRasterValueFromReader(ByteReader reader) {
        return reader.readUnsignedInt();
    }
}
