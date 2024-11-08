package mil.nga.tiff.field.type;

import mil.nga.tiff.internal.FileDirectoryEntry;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Two SLONG’s: the first represents the numerator of a fraction, the second
 * the denominator
 */
abstract sealed class AbstractRationalField extends AbstractFieldType permits UnsignedRationalField, SignedRationalField {
    public AbstractRationalField() {
        super(8);
    }

    @Override
    final public List<Object> getDirectoryEntryValues(ByteReader reader, long typeCount) {
        List<Object> values = new ArrayList<>();

        for (int i = 0; i < typeCount; i++) {
            values.add(readPart(reader));
            values.add(readPart(reader));
        }

        return values;
    }

    @SuppressWarnings("unchecked")
    @Override
    final public int writeDirectoryEntryValues(ByteWriter writer, FileDirectoryEntry entry) throws IOException {
        List<Object> valuesList;
        valuesList = (List<Object>) entry.values();

        int bytesWritten = 0;

        for (Object value : valuesList) {
            writeValue(writer, value);
            bytesWritten += 4;
        }

        return bytesWritten;
    }

    abstract protected Number readPart(ByteReader reader);

    abstract protected void writeValue(ByteWriter writer, Object value) throws IOException;

}
