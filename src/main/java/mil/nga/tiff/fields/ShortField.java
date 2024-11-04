package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.util.TiffConstants;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 16-bit (2-byte) unsigned integer
 */
public final class ShortField extends AbstractRasterFieldType {
    public ShortField() {
        super(2, TiffConstants.SampleFormat.SAMPLE_FORMAT_UNSIGNED_INT);
    }

    @Override
    public Number readValue(ByteReader reader) {
        return reader.readUnsignedShort();
    }

    @Override
    public Number readSample(ByteBuffer buffer) {
        return buffer.getShort() & 0xffff;
    }

    @Override
    public void writeSample(ByteBuffer buffer, Number value) {
        buffer.putShort(value.shortValue());
    }

    @Override
    public void writeSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.putShort(inBuffer.getShort());
    }

    @Override
    protected void writeValue(ByteWriter writer, Object value) throws IOException {
        writer.writeUnsignedShort((int) value);
    }

}
