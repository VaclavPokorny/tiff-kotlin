package mil.nga.tiff.internal

import mil.nga.tiff.field.TagDictionary
import mil.nga.tiff.field.type.GenericFieldType
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.util.TiffConstants
import mil.nga.tiff.util.TiffException

/**
 * TIFF reader
 */
class TiffImageReader(private val reader: ByteReader, private val tagDictionary: TagDictionary) {

    /**
     * Read a TIFF from the byte reader
     *
     * @param cache true to cache tiles and strips
     * @return TIFF image
     */
    fun readTiff(cache: Boolean): TIFFImage {
        // Skip byte order header (determined already)
        reader.setNextByte(2)

        // Validate the TIFF file identifier
        val tiffIdentifier = reader.readUnsignedShort()
        if (tiffIdentifier != TiffConstants.FILE_IDENTIFIER) {
            throw TiffException("Invalid file identifier, not a TIFF")
        }

        // Get the offset in bytes of the first image file internal (IFD)
        val byteOffset = reader.readUnsignedInt()

        // Get the TIFF Image
        return parseTIFFImage(byteOffset, cache)
    }

    /**
     * Parse the TIFF Image with file directories
     *
     * @param byteOffset     byte offset
     * @param cache          true to cache tiles and strips
     * @return TIFF image
     */
    private fun parseTIFFImage(byteOffset: Long, cache: Boolean): TIFFImage {
        var currentByteOffset = byteOffset
        val dirs: MutableList<FileDirectory> = ArrayList()

        // Continue until the byte offset no longer points to another file
        // internal
        while (currentByteOffset != 0L) {
            // Set the next byte to read from

            reader.setNextByte(currentByteOffset)

            // Create the new internal
            val entries: MutableSet<FileDirectoryEntry<*>> = HashSet()

            // Read the number of internal entries
            val numDirectoryEntries = reader.readUnsignedShort()

            // Read each entry and the values
            for (entryCount in 0 until numDirectoryEntries) {
                parseEntry<Any>(entries)
            }

            // Add the file internal
            val fileDirectory = FileDirectory.create(entries, reader, cache, tagDictionary, null)
            dirs.add(fileDirectory)

            // Read the next byte offset location
            currentByteOffset = reader.readUnsignedInt()
        }

        return TIFFImage(dirs, reader.byteOrder)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> parseEntry(entries: MutableSet<FileDirectoryEntry<*>>) {
        // Read the field tag, field type, and type count
        val fieldTagValue = reader.readUnsignedShort()
        val fieldTag = tagDictionary.findTagById(fieldTagValue)

        val fieldTypeValue = reader.readUnsignedShort()
        val fieldType = (tagDictionary.findTypeById(fieldTypeValue) ?: throw TiffException("Unknown field type value $fieldTypeValue")) as GenericFieldType<T>

        val typeCount = reader.readUnsignedInt()

        // Save off the next byte to read location
        val nextByte = reader.nextByte

        // If the value is larger and not stored inline, determine the offset
        if (fieldType.metadata().bytesPerSample * typeCount > 4) {
            val valueOffset = reader.readUnsignedInt()
            reader.setNextByte(valueOffset)
        }

        // Read the field values
        val values = fieldType.readDirectoryEntryValues(reader, typeCount)

        // Create and add a file internal if the tag is recognized.
        entries.add(FileDirectoryEntry(fieldTag, fieldTagValue, fieldType, typeCount, values))

        // Restore the next byte to read location
        reader.setNextByte((nextByte + 4).toLong())
    }
}
