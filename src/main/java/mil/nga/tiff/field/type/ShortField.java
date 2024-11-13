package mil.nga.tiff.field.type;

import java.nio.ByteBuffer;

abstract sealed class ShortField extends NumericFieldType permits UnsignedShortField, SignedShortField {

    @Override
    final protected void writeSample(ByteBuffer buffer, Number value) {
        buffer.putShort(value.shortValue());
    }

    @Override
    final public void writeSample(ByteBuffer outBuffer, ByteBuffer inBuffer) {
        outBuffer.putShort(inBuffer.getShort());
    }

}
