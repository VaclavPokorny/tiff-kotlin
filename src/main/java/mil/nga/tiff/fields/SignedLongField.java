package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffConstants;

/**
 * A 32-bit (4-byte) signed (twos-complement) integer
 */
public final class SignedLongField extends AbstractFieldType {
    public SignedLongField() {
        super(4, TiffConstants.SAMPLE_FORMAT_SIGNED_INT);
    }

    @Override
    public Number readRasterValueFromReader(ByteReader reader) {
        return reader.readInt();
    }
}
