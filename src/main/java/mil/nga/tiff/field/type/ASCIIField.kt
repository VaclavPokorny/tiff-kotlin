package mil.nga.tiff.field.type

import mil.nga.tiff.field.FieldType
import mil.nga.tiff.field.tag.FieldTagType
import mil.nga.tiff.io.ByteReader
import mil.nga.tiff.io.ByteWriter
import mil.nga.tiff.util.TiffException
import java.io.IOException
import java.io.UnsupportedEncodingException

/**
 * 8-bit byte that contains a 7-bit ASCII code; the last byte must be NUL
 * (binary zero)
 */
@FieldType(id = 2, bytesPerSample = 1)
object ASCIIField : GenericFieldType<String> {
    override fun readDirectoryEntryValues(reader: ByteReader, typeCount: Long): List<String> {
        val values: MutableList<String?> = ArrayList()

        for (i in 0..<typeCount) {
            try {
                values.add(reader.readString(1))
            } catch (e: UnsupportedEncodingException) {
                throw TiffException("Failed to read ASCII character", e)
            }
        }

        return combineAsciiCharsIntoStrings(values)
    }

    private fun combineAsciiCharsIntoStrings(values: List<String?>): List<String> {
        val stringValues: MutableList<String> = ArrayList()
        var stringValue = StringBuilder()
        for (value in values) {
            if (value == null) {
                if (stringValue.isNotEmpty()) {
                    stringValues.add(stringValue.toString())
                    stringValue = StringBuilder()
                }
            } else {
                stringValue.append(value)
            }
        }
        return stringValues
    }

    @Throws(IOException::class)
    override fun writeDirectoryEntryValues(
        writer: ByteWriter,
        fieldTag: FieldTagType?,
        typeCount: Long,
        values: List<String>
    ): Int {
        var bytesWritten = 0

        for (value in values) {
            bytesWritten += writer.writeString(value)
            if (bytesWritten < typeCount) {
                val fillerBytes = typeCount - bytesWritten
                writer.writeFillerBytes(fillerBytes)
                bytesWritten += fillerBytes.toInt()
            }
        }

        return bytesWritten
    }
}
