package mil.nga.tiff.fields;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.util.TiffConstants;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A 16-bit (2-byte) signed (twos-complement) integer
 */
public final class SignedShortField extends AbstractRasterFieldType {
    public SignedShortField() {
        super(2, TiffConstants.SampleFormat.SIGNED_INT);
    }

    @Override
    public Number readValue(ByteReader reader) {
        return reader.readShort();
    }

    @Override
    public Number readSample(ByteBuffer buffer) {
        return buffer.getShort();
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
        writer.writeShort((short) value);
    }

}
