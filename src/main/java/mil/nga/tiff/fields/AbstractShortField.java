package mil.nga.tiff.fields;

import mil.nga.tiff.util.SampleFormat;

import java.nio.ByteBuffer;

abstract sealed class AbstractShortField extends AbstractRasterFieldType permits UnsignedShortField, SignedShortField {
    public AbstractShortField(SampleFormat sampleFormat) {
        super(2, sampleFormat);
    }

    @Override
    final public void writeSample(ByteBuffer buffer, Number value) {
        buffer.putShort(value.shortValue());
    }

    @Override
    final public void writeSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.putShort(inBuffer.getShort());
    }

}
