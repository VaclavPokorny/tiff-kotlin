package mil.nga.tiff.field.type;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.internal.FileDirectoryEntry;
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

    @SuppressWarnings("unchecked")
    @Override
    public int writeDirectoryEntryValues(ByteWriter writer, FileDirectoryEntry entry) {
        List<Object> valuesList;
        if (entry.typeCount() == 1 && !entry.fieldTag().isArray()) {
            valuesList = new ArrayList<>();
            valuesList.add(entry.values());
        } else {
            valuesList = (List<Object>) entry.values();
        }

        int bytesWritten = 0;

        for (Object value : valuesList) {
            writer.writeUnsignedByte((short) value);
            bytesWritten += 1;
        }

        return bytesWritten;
    }

}
