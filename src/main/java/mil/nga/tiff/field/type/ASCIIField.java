package mil.nga.tiff.field.type;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.field.tag.FieldTagType;
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
@FieldType(id = 2, bytesPerSample = 1)
public final class ASCIIField implements GenericFieldType<String> {

    @Override
    public List<String> readDirectoryEntryValues(ByteReader reader, long typeCount) {
        List<String> values = new ArrayList<>();

        for (int i = 0; i < typeCount; i++) {
            try {
                values.add(reader.readString(1));
            } catch (UnsupportedEncodingException e) {
                throw new TiffException("Failed to read ASCII character", e);
            }
        }

        return combineAsciiCharsIntoStrings(values);
    }

    private List<String> combineAsciiCharsIntoStrings(List<String> values) {
        List<String> stringValues = new ArrayList<>();
        StringBuilder stringValue = new StringBuilder();
        for (String value : values) {
            if (value == null) {
                if (!stringValue.isEmpty()) {
                    stringValues.add(stringValue.toString());
                    stringValue = new StringBuilder();
                }
            } else {
                stringValue.append(value);
            }
        }
        return stringValues;
    }

    @Override
    public int writeDirectoryEntryValues(ByteWriter writer, FieldTagType fieldTag, long typeCount, List<String> values) throws IOException {
        int bytesWritten = 0;

        for (String value : values) {
            bytesWritten += writer.writeString(value);
            if (bytesWritten < typeCount) {
                long fillerBytes = typeCount - bytesWritten;
                writer.writeFillerBytes(fillerBytes);
                bytesWritten += (int) fillerBytes;
            }
        }

        return bytesWritten;
    }

}
