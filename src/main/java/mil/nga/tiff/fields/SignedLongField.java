package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.util.TiffConstants;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A 32-bit (4-byte) signed (twos-complement) integer
 */
public final class SignedLongField extends AbstractRasterFieldType {
    public SignedLongField() {
        super(4, TiffConstants.SAMPLE_FORMAT_SIGNED_INT);
    }

    @Override
    public Number readValue(ByteReader reader) {
        return reader.readInt();
    }

    @Override
    public Number readSample(ByteBuffer buffer) {
        return buffer.getInt();
    }

    @Override
    public void writeSample(ByteBuffer buffer, Number value) {
        buffer.putInt(value.intValue());
    }

    @Override
    public void writeSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.putInt(inBuffer.getInt());
    }

    @Override
    protected void writeValue(ByteWriter writer, Object value) throws IOException {
        writer.writeInt((int) value);
    }

}
