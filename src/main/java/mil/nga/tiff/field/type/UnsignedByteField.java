package mil.nga.tiff.field.type;

import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

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
    protected Number readSample(ByteBuffer buffer) {
        return (short) (buffer.get() & 0xff);
    }

    @Override
    protected void writeValue(ByteWriter writer, Object value) {
        writer.writeUnsignedByte((short) value);
    }

}
