package mil.nga.tiff.fields;

import mil.nga.tiff.FileDirectoryEntry;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Two SLONG’s: the first represents the numerator of a fraction, the second
 * the denominator
 */
public final class SignedRationalField extends AbstractFieldType {
    public SignedRationalField() {
        super(8);
    }

    @Override
    public List<Object> getDirectoryEntryValues(ByteReader reader, long typeCount) {
        List<Object> values = new ArrayList<>();

        for (int i = 0; i < typeCount; i++) {
            values.add(reader.readInt());
            values.add(reader.readInt());
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
            writer.writeInt((int) value);
            bytesWritten += 4;
        }

        return bytesWritten;
    }

}
