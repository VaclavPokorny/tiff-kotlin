package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffException;

/**
 * 8-bit byte that contains a 7-bit ASCII code; the last byte must be NUL
 * (binary zero)
 */
public final class ASCIIField extends AbstractFieldType {
    public ASCIIField() {
        super(1);
    }

    @Override
    public Number readRasterValueFromReader(ByteReader reader) {
        throw new TiffException("Unsupported raster field type.");
    }
}
