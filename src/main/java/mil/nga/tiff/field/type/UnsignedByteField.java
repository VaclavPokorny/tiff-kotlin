package mil.nga.tiff.field.type;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.nio.ByteBuffer;

/**
 * 8-bit unsigned integer
 */
@FieldType(id = 1, bytesPerSample = 1, sampleFormat = SampleFormat.UNSIGNED_INT)
public final class UnsignedByteField extends ByteField<Short> {

    @Override
    public Short readValue(ByteReader reader) {
        return reader.readUnsignedByte();
    }

    @Override
    protected Short readSample(ByteBuffer buffer) {
        return (short) (buffer.get() & 0xff);
    }

    @Override
    protected int writeValue(ByteWriter writer, Short value) {
        writer.writeUnsignedByte(value);
        return metadata().bytesPerSample();
    }

}
