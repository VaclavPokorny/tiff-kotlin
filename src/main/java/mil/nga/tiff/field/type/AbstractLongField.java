package mil.nga.tiff.field.type;

import mil.nga.tiff.field.type.enumeration.SampleFormat;

import java.nio.ByteBuffer;

abstract sealed class AbstractLongField extends AbstractRasterFieldType permits UnsignedLongField, SignedLongField {
    public AbstractLongField(SampleFormat sampleFormat) {
        super(4, sampleFormat);
    }

    @Override
    final protected void writeSample(ByteBuffer buffer, Number value) {
        buffer.putInt(value.intValue());
    }

    @Override
    final public void writeSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.putInt(inBuffer.getInt());
    }

}
