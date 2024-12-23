package mil.nga.tiff.internal

import mil.nga.tiff.field.FieldTagType
import mil.nga.tiff.field.type.GenericFieldType
import mil.nga.tiff.io.ByteWriter
import mil.nga.tiff.util.TiffConstants
import java.io.IOException

/**
 * TIFF File Directory Entry
 *
 * @param fieldTag   Field Tag Type
 * @param fieldTagId Field Tag Type Id
 * @param fieldType  Field Type
 * @param typeCount  Type Count
 * @param values     Values
 */
@JvmRecord
data class FileDirectoryEntry<T>(
    val fieldTag: FieldTagType?,
    val fieldTagId: Int,
    val fieldType: GenericFieldType<T>,
    val typeCount: Long,
    val values: List<T>
) {
    constructor(fieldTag: FieldTagType, fieldType: GenericFieldType<T>, typeCount: Long, values: List<T>):
        this(fieldTag, fieldTag.id, fieldType, typeCount, values)

    /**
     * Size in bytes of the image file internal entry and its values (not
     * contiguous bytes)
     *
     * @return size in bytes
     */
    fun sizeWithValues(): Long {
        return TiffConstants.IFD_ENTRY_BYTES + sizeOfValues()
    }

    /**
     * Size of the values not included in the internal entry bytes
     *
     * @return size in bytes
     */
    fun sizeOfValues(): Long {
        var size: Long = 0
        val valueBytes = valueBytes()
        if (valueBytes > 4) {
            size = valueBytes
        }
        return size
    }

    fun valueBytes(): Long {
        return fieldType.metadata().bytesPerSample * typeCount
    }

    /**
     * Write file internal entry values
     *
     * @param writer byte writer
     * @return bytes written
     * @throws IOException IO exception
     */
    @Throws(IOException::class)
    fun write(writer: ByteWriter): Int {
        return fieldType.writeDirectoryEntryValues(writer, fieldTag, typeCount, values)
    }

    fun analyze(analyzer: TiffImageAnalyzer) {
        analyzer.describeDirectoryEntry(fieldTag, fieldTagId, fieldType.metadata(), typeCount, values.toString())
    }

}
