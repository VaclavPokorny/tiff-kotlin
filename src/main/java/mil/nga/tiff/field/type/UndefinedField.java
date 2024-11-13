package mil.nga.tiff.field.type;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.field.tag.FieldTagType;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * An 8-bit byte that may contain anything, depending on the definition of
 * the field
 */
@FieldType(id = 7, bytesPerSample = 1)
public final class UndefinedField implements GenericFieldType {

    @Override
    public List<Object> readDirectoryEntryValues(ByteReader reader, long typeCount) {
        List<Object> values = new ArrayList<>();

        for (int i = 0; i < typeCount; i++) {
            values.add(reader.readUnsignedByte());
        }

        return values;
    }

    @Override
    public int writeDirectoryEntryValue(ByteWriter writer, FieldTagType fieldTag, long typeCount, Object value) {
        writer.writeUnsignedByte((short) value);
        return 1;
    }

}
