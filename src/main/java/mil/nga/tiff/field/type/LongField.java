package mil.nga.tiff.field.type;

import java.nio.ByteBuffer;

abstract sealed class LongField extends NumericFieldType permits UnsignedLongField, SignedLongField {

    @Override
    final protected void writeSample(ByteBuffer buffer, Number value) {
        buffer.putInt(value.intValue());
    }

    @Override
    final public void writeSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.putInt(inBuffer.getInt());
    }

}
