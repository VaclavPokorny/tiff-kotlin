package mil.nga.tiff.field.type;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;

/**
 * An 8-bit byte that may contain anything, depending on the definition of
 * the field
 */
@FieldType(id = 7, bytesPerSample = 1)
public final class UndefinedField extends SingleValueFieldType<Short> {

    @Override
    public Short readValue(ByteReader reader) {
        return reader.readUnsignedByte();
    }

    @Override
    protected int writeValue(ByteWriter writer, Short value) throws IOException {
        writer.writeUnsignedByte(value);
        return 1;
    }

}
