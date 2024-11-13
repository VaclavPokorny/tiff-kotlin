package mil.nga.tiff.field.type;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A 32-bit (4-byte) signed (twos-complement) integer
 */
@FieldType(id = 9, bytesPerSample = 4, sampleFormat = SampleFormat.SIGNED_INT)
public final class SignedLongField extends LongField {
    @Override
    public Number readValue(ByteReader reader) {
        return reader.readInt();
    }

    @Override
    protected Number readSample(ByteBuffer buffer) {
        return buffer.getInt();
    }

    @Override
    protected void writeValue(ByteWriter writer, Object value) throws IOException {
        writer.writeInt((int) value);
    }

}
