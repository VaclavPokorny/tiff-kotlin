package mil.nga.tiff.field.type;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * An 8-bit signed (twos-complement) integer
 */
@FieldType(id = 6, bytesPerSample = 1, sampleFormat = SampleFormat.SIGNED_INT)
public final class SignedByteField extends ByteField {

    @Override
    public Number readValue(ByteReader reader) {
        return reader.readByte();
    }

    @Override
    protected Number readSample(ByteBuffer buffer) {
        return buffer.get();
    }

    @Override
    protected void writeValue(ByteWriter writer, Object value) throws IOException {
        writer.writeByte((byte) value);
    }

}
