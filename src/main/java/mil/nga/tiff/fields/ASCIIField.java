package mil.nga.tiff.fields;

import mil.nga.tiff.FieldType;
import mil.nga.tiff.FileDirectoryEntry;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;
import mil.nga.tiff.util.TiffException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 8-bit byte that contains a 7-bit ASCII code; the last byte must be NUL
 * (binary zero)
 */
public final class ASCIIField extends AbstractFieldType {
    public ASCIIField() {
        super(1);
    }

    @Override
    public List<Object> getDirectoryEntryValues(ByteReader reader, long typeCount) {
        List<Object> values = new ArrayList<>();

        for (int i = 0; i < typeCount; i++) {
            try {
                values.add(reader.readString(1));
            } catch (UnsupportedEncodingException e) {
                throw new TiffException("Failed to read ASCII character", e);
            }
        }

        // combine ASCII into strings
        List<Object> stringValues = new ArrayList<>();
        StringBuilder stringValue = new StringBuilder();
        for (Object value : values) {
            if (value == null) {
                if (!stringValue.isEmpty()) {
                    stringValues.add(stringValue.toString());
                    stringValue = new StringBuilder();
                }
            } else {
                stringValue.append(value);
            }
        }
        values = stringValues;

        return values;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int writeValues(ByteWriter writer, FileDirectoryEntry entry) throws IOException {
        List<Object> valuesList;
        if (entry.getTypeCount() == 1 && !entry.getFieldTag().isArray()) {
            valuesList = new ArrayList<>();
            valuesList.add(entry.getValues());
        } else {
            valuesList = (List<Object>) entry.getValues();
        }

        int bytesWritten = 0;

        for (Object value : valuesList) {
            bytesWritten += writer.writeString((String) value);
            if (bytesWritten < entry.getTypeCount()) {
                long fillerBytes = entry.getTypeCount() - bytesWritten;
                writeFillerBytes(writer, fillerBytes);
                bytesWritten += (int) fillerBytes;
            }
        }

        return bytesWritten;
    }

    /**
     * Write filler 0 bytes
     *
     * @param writer byte writer
     * @param count  number of 0 bytes to write
     */
    private void writeFillerBytes(ByteWriter writer, long count) {
        for (long i = 0; i < count; i++) {
            writer.writeUnsignedByte((short) 0);
        }
    }

}
