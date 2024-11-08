package mil.nga.tiff.field.type;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;

/**
 * Two SLONG’s: the first represents the numerator of a fraction, the second
 * the denominator
 */
public final class SignedRationalField extends AbstractRationalField {

    @Override
    protected Number readPart(ByteReader reader) {
        return reader.readInt();
    }

    @Override
    protected void writeValue(ByteWriter writer, Object value) throws IOException {
        writer.writeInt((int) value);
    }

}
