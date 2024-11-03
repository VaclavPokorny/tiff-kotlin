package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffException;

/**
 * An 8-bit byte that may contain anything, depending on the definition of
 * the field
 */
public final class UndefinedField extends AbstractFieldType {
    public UndefinedField() {
        super(1);
    }

    @Override
    public Number readRasterValueFromReader(ByteReader reader) {
        throw new TiffException("Unsupported raster field type.");
    }
}
