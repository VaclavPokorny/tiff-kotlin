package mil.nga.tiff.field.type;

import mil.nga.tiff.field.FieldType;
import mil.nga.tiff.internal.FileDirectoryEntry;
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
     * @param entry  file internal entry
     * @return bytes written
     * @throws IOException IO exception
     */
    int writeDirectoryEntryValues(ByteWriter writer, FileDirectoryEntry entry) throws IOException;

    /**
     * Gathers field type metadata and returns a record with them
     *
     * @return metadata record
     */
    default FieldType metadata() {
        return getClass().getAnnotation(FieldType.class);
    }

}
