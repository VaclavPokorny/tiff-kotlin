package mil.nga.tiff.fields;

import mil.nga.tiff.FileDirectoryEntry;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Two LONGs: the first represents the numerator of a fraction; the second,
 * the denominator
 */
public final class RationalField extends AbstractFieldType {
    public RationalField() {
        super(8);
    }

    @Override
    public List<Object> getDirectoryEntryValues(ByteReader reader, long typeCount) {
        List<Object> values = new ArrayList<>();

        for (int i = 0; i < typeCount; i++) {
            values.add(reader.readUnsignedInt());
            values.add(reader.readUnsignedInt());
        }

        return values;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int writeValues(ByteWriter writer, FileDirectoryEntry entry) throws IOException {
        List<Object> valuesList;
        valuesList = (List<Object>) entry.getValues();

        int bytesWritten = 0;

        for (Object value : valuesList) {
            writer.writeUnsignedInt((long) value);
            bytesWritten += 4;
        }

        return bytesWritten;
    }

}
