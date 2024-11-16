package mil.nga.tiff.field.type;

import java.nio.ByteBuffer;

abstract sealed class LongField<T extends Number> extends NumericFieldType<T> permits UnsignedLongField, SignedLongField {

    @Override
    final protected void writeSample(ByteBuffer buffer, T value) {
        buffer.putInt(value.intValue());
    }

    @Override
    final public void transferSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.putInt(inBuffer.getInt());
    }

}
