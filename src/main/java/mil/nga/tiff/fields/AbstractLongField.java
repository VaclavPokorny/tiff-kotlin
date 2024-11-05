package mil.nga.tiff.fields;

import java.nio.ByteBuffer;

abstract sealed class AbstractLongField extends AbstractRasterFieldType permits UnsignedLongField, SignedLongField {
    public AbstractLongField(int sampleFormat) {
        super(4, sampleFormat);
    }

    @Override
    final public void writeSample(ByteBuffer buffer, Number value) {
        buffer.putInt(value.intValue());
    }

    @Override
    final public void writeSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.putInt(inBuffer.getInt());
    }

}
