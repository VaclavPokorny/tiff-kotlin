package mil.nga.tiff.field.type;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;

/**
 * Two LONGs: the first represents the numerator of a fraction; the second,
 * the denominator
 */
public final class UnsignedRationalField extends AbstractRationalField {

    @Override
    protected Number readPart(ByteReader reader) {
        return reader.readUnsignedInt();
    }

    @Override
    protected void writeValue(ByteWriter writer, Object value) throws IOException {
        writer.writeUnsignedInt((long) value);
    }

}
