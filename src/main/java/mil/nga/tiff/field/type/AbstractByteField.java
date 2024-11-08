package mil.nga.tiff.field.type;

import mil.nga.tiff.field.type.enumeration.SampleFormat;

import java.nio.ByteBuffer;

abstract sealed class AbstractByteField extends AbstractRasterFieldType permits UnsignedByteField, SignedByteField {
    public AbstractByteField(SampleFormat sampleFormat) {
        super(1, sampleFormat);
    }

    @Override
    final public void writeSample(ByteBuffer buffer, Number value) {
        buffer.put(value.byteValue());
    }

    @Override
    final public void writeSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.put(inBuffer.get());
    }

}
