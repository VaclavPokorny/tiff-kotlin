package mil.nga.tiff.field.type;

import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 16-bit (2-byte) unsigned integer
 */
public final class UnsignedShortField extends AbstractShortField {
    public UnsignedShortField() {
        super(SampleFormat.UNSIGNED_INT);
    }

    @Override
    public Number readValue(ByteReader reader) {
        return reader.readUnsignedShort();
    }

    @Override
    public Number readSample(ByteBuffer buffer) {
        return buffer.getShort() & 0xffff;
    }

    @Override
    protected void writeValue(ByteWriter writer, Object value) throws IOException {
        writer.writeUnsignedShort((int) value);
    }

}
