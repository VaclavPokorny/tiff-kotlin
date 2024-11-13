package mil.nga.tiff.field.type;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.field.tag.FieldTagType;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.ByteWriter;

import java.io.IOException;
import java.util.List;

public sealed interface GenericFieldType permits NumericFieldType, ASCIIField, UndefinedField, RationalField {

    /**
     * Get the internal entry values
     *
     * @param reader    byte reader
     * @param typeCount type count
     * @return values
     */
    List<Object> readDirectoryEntryValues(ByteReader reader, long typeCount);

    /**
     * Write file internal entry values
     *
     * @param writer byte writer
     * @param fieldTag entry tag
     * @param typeCount count of values
     * @param values actual value to write
     * @return bytes written
     * @throws IOException IO exception
     */
    int writeDirectoryEntryValue(ByteWriter writer, FieldTagType fieldTag, long typeCount, Object values) throws IOException;

    /**
     * Gathers field type metadata and returns a record with them
     *
     * @return metadata record
     */
    default FieldType metadata() {
        return getClass().getAnnotation(FieldType.class);
    }

}
