package mil.nga.tiff.field.type;

import mil.nga.tiff.field.type.enumeration.SampleFormat;

import java.nio.ByteBuffer;

abstract sealed class AbstractShortField extends AbstractRasterFieldType permits UnsignedShortField, SignedShortField {
    public AbstractShortField(SampleFormat sampleFormat) {
        super(2, sampleFormat);
    }

    @Override
    final protected void writeSample(ByteBuffer buffer, Number value) {
        buffer.putShort(value.shortValue());
    }

    @Override
    final public void writeSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.putShort(inBuffer.getShort());
    }

}
