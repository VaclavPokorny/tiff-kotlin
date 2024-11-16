package mil.nga.tiff.field.type;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 16-bit (2-byte) unsigned integer
 */
@FieldType(id = 3, bytesPerSample = 2, sampleFormat = SampleFormat.UNSIGNED_INT)
public final class UnsignedShortField extends ShortField<Integer> {

    @Override
    public Integer readValue(ByteReader reader) {
        return reader.readUnsignedShort();
    }

    @Override
    protected Integer readSample(ByteBuffer buffer) {
        return buffer.getShort() & 0xffff;
    }

    @Override
    protected int writeValue(ByteWriter writer, Integer value) throws IOException {
        writer.writeUnsignedShort(value);
        return metadata().bytesPerSample();
    }

}
