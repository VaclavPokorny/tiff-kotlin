package mil.nga.tiff.fields;

import java.nio.ByteBuffer;

abstract sealed class AbstractByteField extends AbstractRasterFieldType permits UnsignedByteField, SignedByteField {
    public AbstractByteField(int sampleFormat) {
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
