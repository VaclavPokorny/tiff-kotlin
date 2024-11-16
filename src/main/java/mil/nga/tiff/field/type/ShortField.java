package mil.nga.tiff.field.type;

import java.nio.ByteBuffer;

abstract sealed class ShortField<T extends Number> extends NumericFieldType<T> permits UnsignedShortField, SignedShortField {

    @Override
    final protected void writeSample(ByteBuffer buffer, T value) {
        buffer.putShort(value.shortValue());
    }

    @Override
    final public void transferSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.putShort(inBuffer.getShort());
    }

}
