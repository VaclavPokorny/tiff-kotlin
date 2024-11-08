package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.util.SampleFormat;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 32-bit (4-byte) unsigned integer
 */
public final class UnsignedLongField extends AbstractLongField {
    public UnsignedLongField() {
        super(SampleFormat.UNSIGNED_INT);
    }

    @Override
    public Number readValue(ByteReader reader) {
        return reader.readUnsignedInt();
    }

    @Override
    public Number readSample(ByteBuffer buffer) {
        return buffer.getInt() & 0xffffffffL;
    }

    @Override
    protected void writeValue(ByteWriter writer, Object value) throws IOException {
        writer.writeUnsignedInt((long) value);
    }

}
