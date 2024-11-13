package mil.nga.tiff.field.type;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;

/**
 * Two SLONG’s: the first represents the numerator of a fraction, the second
 * the denominator
 */
@FieldType(id = 10, bytesPerSample = 8, multivalue = true)
public final class SignedRationalField extends RationalField {

    @Override
    protected Number readPart(ByteReader reader) {
        return reader.readInt();
    }

    @Override
    protected void writeValue(ByteWriter writer, Object value) throws IOException {
        writer.writeInt((int) value);
    }

}
