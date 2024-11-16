package mil.nga.tiff.field.type;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A 32-bit (4-byte) signed (twos-complement) integer
 */
@FieldType(id = 9, bytesPerSample = 4, sampleFormat = SampleFormat.SIGNED_INT)
public final class SignedLongField extends LongField<Integer> {
    @Override
    public Integer readValue(ByteReader reader) {
        return reader.readInt();
    }

    @Override
    protected Integer readSample(ByteBuffer buffer) {
        return buffer.getInt();
    }

    @Override
    protected int writeValue(ByteWriter writer, Integer value) throws IOException {
        writer.writeInt(value);
        return metadata().bytesPerSample();
    }

}
