package mil.nga.tiff;

import mil.nga.tiff.io.ByteReader;
import mil.nga.tiff.io.IOUtils;
import mil.nga.tiff.util.TiffConstants;
import mil.nga.tiff.util.TiffException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * TIFF reader
 *
 * @author osbornb
 */
public class TiffReader {

    /**
     * Read a TIFF from a file
     *
     * @param file TIFF file
     * @return TIFF image
     * @throws IOException upon failure to read
     */
    public static TIFFImage readTiff(File file) throws IOException {
        return readTiff(file, false);
    }

    /**
     * Read a TIFF from a file
     *
     * @param file  TIFF file
     * @param cache true to cache tiles and strips
     * @return TIFF image
     * @throws IOException upon failure to read
     */
    public static TIFFImage readTiff(File file, boolean cache) throws IOException {
        byte[] bytes = IOUtils.fileBytes(file);
        return readTiff(bytes, cache);
    }

    /**
     * Read a TIFF from an input stream
     *
     * @param stream TIFF input stream
     * @return TIFF image
     * @throws IOException upon failure to read
     */
    public static TIFFImage readTiff(InputStream stream) throws IOException {
        return readTiff(stream, false);
    }

    /**
     * Read a TIFF from an input stream
     *
     * @param stream TIFF input stream
     * @param cache  true to cache tiles and strips
     * @return TIFF image
     * @throws IOException upon failure to read
     */
    public static TIFFImage readTiff(InputStream stream, boolean cache) throws IOException {
        byte[] bytes = IOUtils.streamBytes(stream);
        return readTiff(bytes, cache);
    }

    /**
     * Read a TIFF from the bytes
     *
     * @param bytes TIFF bytes
     * @return TIFF image
     */
    public static TIFFImage readTiff(byte[] bytes) {
        return readTiff(bytes, false);
    }

    /**
     * Read a TIFF from the bytes
     *
     * @param bytes TIFF bytes
     * @param cache true to cache tiles and strips
     * @return TIFF image
     */
    public static TIFFImage readTiff(byte[] bytes, boolean cache) {
        ByteReader reader = new ByteReader(bytes);
        return readTiff(reader, cache);
    }

    /**
     * Read a TIFF from the byte reader
     *
     * @param reader byte reader
     * @return TIFF image
     */
    public static TIFFImage readTiff(ByteReader reader) {
        return readTiff(reader, false);
    }

    /**
     * Read a TIFF from the byte reader
     *
     * @param reader byte reader
     * @param cache  true to cache tiles and strips
     * @return TIFF image
     */
    public static TIFFImage readTiff(ByteReader reader, boolean cache) {

        // Read the 2 bytes of byte order
        String byteOrderString;
        try {
            byteOrderString = reader.readString(2);
        } catch (UnsupportedEncodingException e) {
            throw new TiffException("Failed to read byte order", e);
        }

        // Determine the byte order
        ByteOrder byteOrder = switch (byteOrderString) {
            case TiffConstants.BYTE_ORDER_LITTLE_ENDIAN -> ByteOrder.LITTLE_ENDIAN;
            case TiffConstants.BYTE_ORDER_BIG_ENDIAN -> ByteOrder.BIG_ENDIAN;
            default -> throw new TiffException("Invalid byte order: " + byteOrderString);
        };
        reader.setByteOrder(byteOrder);

        // Validate the TIFF file identifier
        int tiffIdentifier = reader.readUnsignedShort();
        if (tiffIdentifier != TiffConstants.FILE_IDENTIFIER) {
            throw new TiffException("Invalid file identifier, not a TIFF");
        }

        // Get the offset in bytes of the first image file directory (IFD)
        long byteOffset = reader.readUnsignedInt();

        // Get the TIFF Image
        return parseTIFFImage(reader, byteOffset, cache);
    }

    /**
     * Parse the TIFF Image with file directories
     *
     * @param reader     byte reader
     * @param byteOffset byte offset
     * @param cache      true to cache tiles and strips
     * @return TIFF image
     */
    private static TIFFImage parseTIFFImage(ByteReader reader, long byteOffset, boolean cache) {

        TIFFImage tiffImage = new TIFFImage();

        // Continue until the byte offset no longer points to another file
        // directory
        while (byteOffset != 0) {

            // Set the next byte to read from
            reader.setNextByte(byteOffset);

            // Create the new directory
            SortedSet<FileDirectoryEntry> entries = new TreeSet<>();

            // Read the number of directory entries
            int numDirectoryEntries = reader.readUnsignedShort();

            // Read each entry and the values
            for (short entryCount = 0; entryCount < numDirectoryEntries; entryCount++) {

                // Read the field tag, field type, and type count
                int fieldTagValue = reader.readUnsignedShort();
                FieldTagType fieldTag = FieldTagType.getById(fieldTagValue);

                int fieldTypeValue = reader.readUnsignedShort();
                FieldType fieldType = FieldType.getFieldType(fieldTypeValue);
                if (fieldType == null) {
                    throw new TiffException("Unknown field type value " + fieldTypeValue);
                }

                long typeCount = reader.readUnsignedInt();

                // Save off the next byte to read location
                int nextByte = reader.getNextByte();

                // Read the field values
                Object values = readFieldValues(reader, fieldTag, fieldType, typeCount);

                // Create and add a file directory if the tag is recognized.
                if (fieldTag != null) {
                    FileDirectoryEntry entry = new FileDirectoryEntry(fieldTag, fieldType, typeCount, values);
                    entries.add(entry);
                }

                // Restore the next byte to read location
                reader.setNextByte(nextByte + 4);
            }

            // Add the file directory
            FileDirectory fileDirectory = new FileDirectory(entries, reader, cache);
            tiffImage.add(fileDirectory);

            // Read the next byte offset location
            byteOffset = reader.readUnsignedInt();
        }

        return tiffImage;
    }

    /**
     * Read the field values
     *
     * @param reader    byte reader
     * @param fieldTag  field tag type
     * @param fieldType field type
     * @param typeCount type count
     * @return values
     */
    private static Object readFieldValues(ByteReader reader, FieldTagType fieldTag, FieldType fieldType, long typeCount) {

        // If the value is larger and not stored inline, determine the offset
        if (fieldType.getBytes() * typeCount > 4) {
            long valueOffset = reader.readUnsignedInt();
            reader.setNextByte(valueOffset);
        }

        // Read the directory entry values
        List<Object> valuesList = getValues(reader, fieldType, typeCount);

        // Get the single or array values
        Object values;
        if (typeCount == 1 && fieldTag != null && !fieldTag.isArray() && !(fieldType == FieldType.RATIONAL || fieldType == FieldType.SRATIONAL)) {
            values = valuesList.getFirst();
        } else {
            values = valuesList;
        }

        return values;
    }

    /**
     * Get the directory entry values
     *
     * @param reader    byte reader
     * @param fieldType field type
     * @param typeCount type count
     * @return values
     */
    private static List<Object> getValues(ByteReader reader, FieldType fieldType, long typeCount) {
        return fieldType.getDirectoryEntryValues(reader, typeCount);
    }

}
