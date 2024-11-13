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
public final class FloatField extends NumericFieldType {

    @Override
    public Number readValue(ByteReader reader) {
        return reader.readFloat();
    }

    @Override
    protected Number readSample(ByteBuffer buffer) {
        return buffer.getFloat();
    }

    @Override
    protected void writeSample(ByteBuffer buffer, Number value) {
        buffer.putFloat(value.floatValue());
    }

    @Override
    public void writeSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.putFloat(inBuffer.getFloat());
    }

    @Override
    protected void writeValue(ByteWriter writer, Object value) throws IOException {
        writer.writeFloat((float) value);
    }

}
