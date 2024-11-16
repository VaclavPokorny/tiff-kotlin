package mil.nga.tiff.field.type;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.field.type.enumeration.SampleFormat;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Double precision (8-byte) IEEE format
 */
@FieldType(id = 12, bytesPerSample = 8, sampleFormat = SampleFormat.FLOAT)
public final class DoubleField extends NumericFieldType<Double> {

    @Override
    public Double readValue(ByteReader reader) {
        return reader.readDouble();
    }

    @Override
    protected Double readSample(ByteBuffer buffer) {
        return buffer.getDouble();
    }

    @Override
    protected void writeSample(ByteBuffer buffer, Double value) {
        buffer.putDouble(value);
    }

    @Override
    public void transferSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.putDouble(inBuffer.getDouble());
    }

    @Override
    protected int writeValue(ByteWriter writer, Double value) throws IOException {
        writer.writeDouble(value);
        return metadata().bytesPerSample();
    }

}
