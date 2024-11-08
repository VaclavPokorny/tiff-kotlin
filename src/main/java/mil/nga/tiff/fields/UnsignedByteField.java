package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.util.SampleFormat;

import java.nio.ByteBuffer;

/**
 * 8-bit unsigned integer
 */
public final class UnsignedByteField extends AbstractByteField {
    public UnsignedByteField() {
        super(SampleFormat.UNSIGNED_INT);
    }

    @Override
    public Number readValue(ByteReader reader) {
        return reader.readUnsignedByte();
    }

    @Override
    public Number readSample(ByteBuffer buffer) {
        return (short) (buffer.get() & 0xff);
    }

    @Override
    protected void writeValue(ByteWriter writer, Object value) {
        writer.writeUnsignedByte((short) value);
    }

}
