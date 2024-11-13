package mil.nga.tiff.internal;

import mil.nga.tiff.field.tag.FieldTagType;
import mil.nga.tiff.field.FieldTypeDictionary;
import mil.nga.tiff.field.type.GenericFieldType;
import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.util.TiffByteOrder;
import mil.nga.tiff.util.TiffConstants;
import mil.nga.tiff.util.TiffException;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * TIFF reader
 *
 * @author osbornb
 */
public class TiffImageReader {

    private final ByteReader reader;
    private final FieldTypeDictionary typeDictionary;

    public TiffImageReader(ByteReader reader, FieldTypeDictionary typeDictionary) {
        this.reader = reader;
        this.typeDictionary = typeDictionary;
    }

    /**
     * Read a TIFF from the byte reader
     *
     * @param cache true to cache tiles and strips
     * @return TIFF image
     */
    public TIFFImage readTiff(boolean cache) {
        // Skip byte order header (determined already)
        reader.setNextByte(2);

        // Validate the TIFF file identifier
        int tiffIdentifier = reader.readUnsignedShort();
        if (tiffIdentifier != TiffConstants.FILE_IDENTIFIER) {
            throw new TiffException("Invalid file identifier, not a TIFF");
        }

        // Get the offset in bytes of the first image file internal (IFD)
        long byteOffset = reader.readUnsignedInt();

        // Get the TIFF Image
        return parseTIFFImage(byteOffset, cache);
    }

    /**
     * Parse the TIFF Image with file directories
     *
     * @param byteOffset     byte offset
     * @param cache          true to cache tiles and strips
     * @return TIFF image
     */
    private TIFFImage parseTIFFImage(long byteOffset, boolean cache) {

        List<FileDirectory> dirs = new ArrayList<>();

        // Continue until the byte offset no longer points to another file
        // internal
        while (byteOffset != 0) {

            // Set the next byte to read from
            reader.setNextByte(byteOffset);

            // Create the new internal
            SortedSet<FileDirectoryEntry> entries = new TreeSet<>();

            // Read the number of internal entries
            int numDirectoryEntries = reader.readUnsignedShort();

            // Read each entry and the values
            for (short entryCount = 0; entryCount < numDirectoryEntries; entryCount++) {

                // Read the field tag, field type, and type count
                int fieldTagValue = reader.readUnsignedShort();
                FieldTagType fieldTag = FieldTagType.getById(fieldTagValue).orElse(null);
                if (fieldTag == null) {
                    System.out.println("Unrecognized tag: " + fieldTagValue);
                }

                int fieldTypeValue = reader.readUnsignedShort();
                GenericFieldType fieldType = typeDictionary.findById(fieldTypeValue);
                if (fieldType == null) {
                    throw new TiffException("Unknown field type value " + fieldTypeValue);
                }

                long typeCount = reader.readUnsignedInt();

                // Save off the next byte to read location
                int nextByte = reader.getNextByte();

                // Read the field values
                Object values = readFieldValues(fieldTag, fieldType, typeCount);

                // Create and add a file internal if the tag is recognized.
                if (fieldTag != null) {
                    FileDirectoryEntry entry = new FileDirectoryEntry(fieldTag, fieldType, typeCount, values);
                    entries.add(entry);
                }

                // Restore the next byte to read location
                reader.setNextByte(nextByte + 4);
            }

            // Add the file internal
            FileDirectory fileDirectory = new FileDirectory(entries, reader, cache, typeDictionary);
            dirs.add(fileDirectory);

            // Read the next byte offset location
            byteOffset = reader.readUnsignedInt();
        }

        return new TIFFImage(dirs, reader.getByteOrder());
    }

    /**
     * Read the field values
     *
     * @param fieldTag  field tag type
     * @param fieldType field type
     * @param typeCount type count
     * @return values
     */
    private Object readFieldValues(FieldTagType fieldTag, GenericFieldType fieldType, long typeCount) {

        // If the value is larger and not stored inline, determine the offset
        if (fieldType.metadata().bytesPerSample() * typeCount > 4) {
            long valueOffset = reader.readUnsignedInt();
            reader.setNextByte(valueOffset);
        }

        // Read the internal entry values
        List<Object> valuesList = fieldType.readDirectoryEntryValues(reader, typeCount);

        // Get the single or array values
        Object values;
        if (typeCount == 1 && fieldTag != null && !fieldTag.isArray()) {
            values = valuesList.getFirst();
        } else {
            values = valuesList;
        }

        return values;
    }

}
