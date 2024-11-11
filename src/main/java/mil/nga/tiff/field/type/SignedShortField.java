package mil.nga.tiff.field.type;

import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A 16-bit (2-byte) signed (twos-complement) integer
 */
public final class SignedShortField extends AbstractShortField {
    public SignedShortField() {
        super(SampleFormat.SIGNED_INT);
    }

    @Override
    public Number readValue(ByteReader reader) {
        return reader.readShort();
    }

    @Override
    protected Number readSample(ByteBuffer buffer) {
        return buffer.getShort();
    }

    @Override
    protected void writeValue(ByteWriter writer, Object value) throws IOException {
        writer.writeShort((short) value);
    }

}
