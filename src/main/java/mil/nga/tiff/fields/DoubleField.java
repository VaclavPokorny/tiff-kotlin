package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffConstants;

/**
 * Double precision (8-byte) IEEE format
 */
public final class DoubleField extends AbstractFieldType {
    public DoubleField() {
        super(8, TiffConstants.SAMPLE_FORMAT_FLOAT);
    }

    @Override
    public Number readRasterValueFromReader(ByteReader reader) {
        return reader.readDouble();
    }
}
