package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.util.TiffConstants;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 32-bit (4-byte) unsigned integer
 */
public final class LongField extends AbstractRasterFieldType {
    public LongField() {
        super(4, TiffConstants.SAMPLE_FORMAT_UNSIGNED_INT);
    }

    @Override
    public Number readValue(ByteReader reader) {
        return reader.readUnsignedInt();
    }

    @Override
    public Number readSample(ByteBuffer buffer) {
        return buffer.getInt() & 0xffffffffL;
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
        writer.writeUnsignedInt((long) value);
    }

}