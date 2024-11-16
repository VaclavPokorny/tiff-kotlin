package mil.nga.tiff.field.type;

import mil.nga.tiff.domain.SignedRational;
import mil.nga.tiff.domain.UnsignedRational;
import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;

/**
 * Two SLONG’s: the first represents the numerator of a fraction, the second
 * the denominator
 */
@FieldType(id = 10, bytesPerSample = 8)
public final class SignedRationalField extends RationalField<SignedRational> {

    @Override
    public SignedRational readValue(ByteReader reader) {
        return new SignedRational(
            reader.readInt(),
            reader.readInt()
        );
    }

    @Override
    protected int writeValue(ByteWriter writer, SignedRational value) throws IOException {
        writer.writeInt(value.numerator());
        writer.writeInt(value.denominator());
        return metadata().bytesPerSample();
    }

}
