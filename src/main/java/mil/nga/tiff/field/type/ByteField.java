package mil.nga.tiff.field.type;

import java.nio.ByteBuffer;

abstract sealed class ByteField extends NumericFieldType permits UnsignedByteField, SignedByteField {

    @Override
    final protected void writeSample(ByteBuffer buffer, Number value) {
        buffer.put(value.byteValue());
    }

    @Override
    final public void writeSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.put(inBuffer.get());
    }

}
