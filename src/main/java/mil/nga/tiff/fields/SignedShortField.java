package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffConstants;

/**
 * A 16-bit (2-byte) signed (twos-complement) integer
 */
public final class SignedShortField extends AbstractFieldType {
    public SignedShortField() {
        super(2, TiffConstants.SAMPLE_FORMAT_SIGNED_INT);
    }

    @Override
    public Number readRasterValueFromReader(ByteReader reader) {
        return reader.readShort();
    }
}
