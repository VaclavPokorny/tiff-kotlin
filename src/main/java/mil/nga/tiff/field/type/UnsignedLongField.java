package mil.nga.tiff.field.type;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 32-bit (4-byte) unsigned integer
 */
@FieldType(id = 4, bytesPerSample = 4, sampleFormat = SampleFormat.UNSIGNED_INT)
public final class UnsignedLongField extends LongField {
    @Override
    public Number readValue(ByteReader reader) {
        return reader.readUnsignedInt();
    }

    @Override
    protected Number readSample(ByteBuffer buffer) {
        return buffer.getInt() & 0xffffffffL;
    }

    @Override
    protected void writeValue(ByteWriter writer, Object value) throws IOException {
        writer.writeUnsignedInt((long) value);
    }

}
