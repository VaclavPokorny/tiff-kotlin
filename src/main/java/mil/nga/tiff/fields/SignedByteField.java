package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffConstants;

/**
 * An 8-bit signed (twos-complement) integer
 */
public final class SignedByteField extends AbstractFieldType {
    public SignedByteField() {
        super(1, TiffConstants.SAMPLE_FORMAT_SIGNED_INT);
    }

    @Override
    public Number readRasterValueFromReader(ByteReader reader) {
        return reader.readByte();
    }
}
