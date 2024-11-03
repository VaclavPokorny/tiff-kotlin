package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffException;

/**
 * Two SLONG’s: the first represents the numerator of a fraction, the second
 * the denominator
 */
public final class SignedRationalField extends AbstractFieldType {
    public SignedRationalField() {
        super(8);
    }

    @Override
    public Number readRasterValueFromReader(ByteReader reader) {
        throw new TiffException("Unsupported raster field type.");
    }
}
