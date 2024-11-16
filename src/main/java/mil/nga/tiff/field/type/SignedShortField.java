package mil.nga.tiff.field.type;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A 16-bit (2-byte) signed (twos-complement) integer
 */
@FieldType(id = 8, bytesPerSample = 2, sampleFormat = SampleFormat.SIGNED_INT)
public final class SignedShortField extends ShortField<Short> {

    @Override
    public Short readValue(ByteReader reader) {
        return reader.readShort();
    }

    @Override
    protected Short readSample(ByteBuffer buffer) {
        return buffer.getShort();
    }

    @Override
    protected int writeValue(ByteWriter writer, Short value) throws IOException {
        writer.writeShort(value);
        return metadata().bytesPerSample();
    }

}
