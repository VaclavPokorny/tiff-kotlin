package mil.nga.tiff.field.type;

import java.nio.ByteBuffer;

abstract sealed class ByteField<T extends Number> extends NumericFieldType<T> permits UnsignedByteField, SignedByteField {

    @Override
    final protected void writeSample(ByteBuffer buffer, T value) {
        buffer.put(value.byteValue());
    }

    @Override
    final public void transferSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.put(inBuffer.get());
    }

}
