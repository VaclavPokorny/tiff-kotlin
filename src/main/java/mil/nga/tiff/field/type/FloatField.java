package mil.nga.tiff.field.type;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Single precision (4-byte) IEEE format
 */
@FieldType(id = 11, bytesPerSample = 4, sampleFormat = SampleFormat.FLOAT)
public final class FloatField extends NumericFieldType<Float> {

    @Override
    public Float readValue(ByteReader reader) {
        return reader.readFloat();
    }

    @Override
    protected Float readSample(ByteBuffer buffer) {
        return buffer.getFloat();
    }

    @Override
    protected void writeSample(ByteBuffer buffer, Float value) {
        buffer.putFloat(value);
    }

    @Override
    public void transferSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.putFloat(inBuffer.getFloat());
    }

    @Override
    protected int writeValue(ByteWriter writer, Float value) throws IOException {
        writer.writeFloat(value);
        return metadata().bytesPerSample();
    }

}
