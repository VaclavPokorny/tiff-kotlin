package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffException;

/**
 * Two LONGs: the first represents the numerator of a fraction; the second,
 * the denominator
 */
public final class RationalField extends AbstractFieldType {
    public RationalField() {
        super(8);
    }

    @Override
    public Number readRasterValueFromReader(ByteReader reader) {
        throw new TiffException("Unsupported raster field type.");
    }
}
