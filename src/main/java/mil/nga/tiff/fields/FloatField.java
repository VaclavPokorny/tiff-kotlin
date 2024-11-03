package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffConstants;

/**
 * Single precision (4-byte) IEEE format
 */
public final class FloatField extends AbstractFieldType {
    public FloatField() {
        super(4, TiffConstants.SAMPLE_FORMAT_FLOAT);
    }

    @Override
    public Number readRasterValueFromReader(ByteReader reader) {
        return reader.readFloat();
    }
}
