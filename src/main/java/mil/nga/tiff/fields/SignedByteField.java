package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.util.TiffConstants;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * An 8-bit signed (twos-complement) integer
 */
public final class SignedByteField extends AbstractRasterFieldType {
    public SignedByteField() {
        super(1, TiffConstants.SampleFormat.SIGNED_INT);
    }

    @Override
    public Number readValue(ByteReader reader) {
        return reader.readByte();
    }

    @Override
    public Number readSample(ByteBuffer buffer) {
        return buffer.get();
    }

    @Override
    public void writeSample(ByteBuffer buffer, Number value) {
        buffer.put(value.byteValue());
    }

    @Override
    public void writeSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.put(inBuffer.get());
    }

    @Override
    protected void writeValue(ByteWriter writer, Object value) throws IOException {
        writer.writeByte((byte) value);
    }

}
