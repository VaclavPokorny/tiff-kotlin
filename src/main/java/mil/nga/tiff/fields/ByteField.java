package mil.nga.tiff.fields;

import mil.nga.tiff.FieldType;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.util.TiffConstants;
import mil.nga.tiff.util.TiffException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 8-bit unsigned integer
 */
public final class ByteField extends AbstractRasterFieldType {
    public ByteField() {
        super(1, TiffConstants.SAMPLE_FORMAT_UNSIGNED_INT);
    }

    @Override
    public Number readValue(ByteReader reader) {
        return reader.readUnsignedByte();
    }

    @Override
    public Number readSample(ByteBuffer buffer) {
        return (short) (buffer.get() & 0xff);
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
    protected void writeValue(ByteWriter writer, Object value) {
        writer.writeUnsignedByte((short) value);
    }

}
