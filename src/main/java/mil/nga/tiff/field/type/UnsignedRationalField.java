package mil.nga.tiff.field.type;

import mil.nga.tiff.domain.UnsignedRational;
import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;
import org.joou.UInteger;

import java.io.IOException;

/**
 * Two LONGs: the first represents the numerator of a fraction; the second,
 * the denominator
 */
@FieldType(id = 5, bytesPerSample = 8)
public final class UnsignedRationalField extends RationalField<UInteger, UnsignedRational> {

    @Override
    protected UnsignedRational readValue(ByteReader reader) {
        return new UnsignedRational(
            UInteger.valueOf(reader.readUnsignedInt()),
            UInteger.valueOf(reader.readUnsignedInt())
        );
    }

    @Override
    protected int writeValue(ByteWriter writer, UnsignedRational value) throws IOException {
        writer.writeUnsignedInt(value.numerator().longValue());
        writer.writeUnsignedInt(value.denominator().longValue());
        return 8;
    }

}
