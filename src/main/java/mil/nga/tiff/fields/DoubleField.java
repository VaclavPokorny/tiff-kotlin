package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.util.TiffConstants;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Double precision (8-byte) IEEE format
 */
public final class DoubleField extends AbstractRasterFieldType {
    public DoubleField() {
        super(8, TiffConstants.SampleFormat.FLOAT);
    }

    @Override
    public Number readValue(ByteReader reader) {
        return reader.readDouble();
    }

    @Override
    public Number readSample(ByteBuffer buffer) {
        return buffer.getDouble();
    }

    @Override
    public void writeSample(ByteBuffer buffer, Number value) {
        buffer.putDouble(value.doubleValue());
    }

    @Override
    public void writeSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.putDouble(inBuffer.getDouble());
    }

    @Override
    protected void writeValue(ByteWriter writer, Object value) throws IOException {
        writer.writeDouble((double) value);
    }

}
